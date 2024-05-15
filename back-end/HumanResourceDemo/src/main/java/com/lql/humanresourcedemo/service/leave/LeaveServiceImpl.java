package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.byDate;
import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.utility.MappingUtility.leaveRequestToResponse;
import static com.lql.humanresourcedemo.utility.MappingUtility.toLeaveRequest;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;


    @Override
    @Transactional
    public LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request) {

        if(!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
        if(request.type().equals(LeaveType.PAID)) {
            OnLyLeaveDays leaveDays = employeeRepository.findById(employeeId, OnLyLeaveDays.class)
                    .orElseThrow(() -> new EmployeeException(employeeId));
            if(leaveDays.leaveDays() < 1)
                throw new LeaveRequestException("Requesting a paid leave day but not enough leave day left");
        }
        if(leaveRepository.exists(byEmployeeId(employeeId).and(byDate(request.leaveDate())))) {
            throw new LeaveRequestException("Already have a leave request on that date");
        }
        LeaveRequest leaveRequest = toLeaveRequest(employeeRepository.getReferenceById(employeeId), request);

        return leaveRequestToResponse(leaveRepository.save(leaveRequest));
    }


    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Pageable pageRequest) {
        return leaveRepository.findBy(byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest)).map(MappingUtility::leaveRequestToResponse);
    }

    @Override
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date) {
        return leaveRepository.findBy(byEmployeeId(employeeId).and(byDate(date)), FluentQuery.FetchableFluentQuery::first).map(MappingUtility::leaveRequestToResponse);
    }
}
