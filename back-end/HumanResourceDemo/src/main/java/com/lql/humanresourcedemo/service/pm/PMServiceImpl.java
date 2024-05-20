package com.lql.humanresourcedemo.service.pm;


import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.util.MappingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.byId;
import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.byPmId;

@Service
@RequiredArgsConstructor
public class PMServiceImpl implements PMService {

    private final AttendanceRepository attendanceRepository;
    private final ValidateService validateService;
    private final EmployeeRepository employeeRepository;
    private final LeaveService leaveService;
    @Override
    @Transactional
    // give attendance to employees in manage
    public List<Attendance> checkAttendance(Long pmId, CheckAttendanceRequest request) {
        validateService.requireExistsEmployee(pmId);
        // get all employees in manage
        List<Long> empIdsInManage = employeeRepository.findAllIdByManagedById(pmId);
        LocalDate now = LocalDate.now();

        request.attendanceDetails()
                .forEach(detail -> {
                    // check if employees in the give attendance request exists or not
                    if (!employeeRepository.existsById(detail.employeeId())) {
                        throw new EmployeeException(detail.employeeId());
                    }
                    // check if employees in the give attendance request in their manage or not
                    if (!empIdsInManage.contains(detail.employeeId())) {
                        throw new EmployeeException("You cannot give attendance to people who are not in your manage");
                    }
                    // check if employees in the give attendance request already have an attendance record for today or not
                    if (attendanceRepository.existsByEmployeeIdAndDate(detail.employeeId(), now)) {
                        throw new EmployeeException("Employee %s already have an attendance record".formatted(detail.employeeId()));

                    }
                });
        // check successfully, create new attendance record for each employee
        return request.attendanceDetails()
                .stream()
                .map(at -> MappingUtil.toAttendance(at, now, employeeRepository.getReferenceById(at.employeeId())))
                .map(attendanceRepository::save)
                .toList();
    }

    @Override
    @Transactional
    public LeaveResponse handleLeaveRequest(Long pmId, HandleLeaveRequest request) {
        return leaveService.handleLeaveRequest(pmId, request);
    }

    @Override
    public Page<GetProfileResponse> getAllEmployee(Long pmId, Pageable pageRequest) {
        validateService.requireExistsEmployee(pmId);
        return employeeRepository.findBy(byPmId(pmId).or(byId(pmId)), p -> p.sortBy(pageRequest.getSort()).page(pageRequest))
                .map(MappingUtil::employeeToProfileResponse);
    }

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long pmId, Pageable pageRequest) {
        return leaveService.getAllLeaveRequest(pmId, Role.PM, pageRequest);
    }

}
