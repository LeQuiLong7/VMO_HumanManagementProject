package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.HelperUtility.buildPageRequest;
import static com.lql.humanresourcedemo.utility.MappingUtility.leaveRequestToResponse;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    private final ValidateService validateService;


    @Override
    @Transactional
    public LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request) {


        OnLyLeaveDays leaveDays = employeeRepository.findById(employeeId, OnLyLeaveDays.class)
                .orElseThrow(() -> new EmployeeException(employeeId));


        if(request.type().equals(LeaveType.PAID) && leaveDays.leaveDays() < 1) {
            throw new LeaveRequestException("Requesting a paid leave day but not enough leave day left");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employeeRepository.getReferenceById(employeeId))
                .date(request.leaveDate())
                .type(request.type())
                .status(LeaveStatus.PROCESSING)
                .reason(request.reason())
                .build();

        return leaveRequestToResponse(leaveRepository.save(leaveRequest));


    }

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long employeeId, String page, String pageSize, List<String> properties, List<String> orders) {
        validateService.validatePageRequest(page, pageSize, properties, orders, LeaveRequest.class);

        Pageable pageRequest = buildPageRequest(Integer.parseInt(page), Integer.parseInt(pageSize), properties, orders, LeaveRequest.class);
        return leaveRepository.findAllByEmployeeId(employeeId, pageRequest).map(MappingUtility::leaveRequestToResponse);
    }

    @Override
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date) {
        return leaveRepository.findByEmployeeIdAndDate(employeeId,date).map(MappingUtility::leaveRequestToResponse);
//        return Optional.empty();
    }
}
