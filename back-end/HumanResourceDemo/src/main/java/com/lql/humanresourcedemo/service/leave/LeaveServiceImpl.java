package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.utility.MappingUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.byDate;
import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.byEmployeeId;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{

    private final LeaveRepository leaveRepository;

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Pageable pageRequest) {
        return leaveRepository.findBy(byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest)).map(MappingUtility::leaveRequestToResponse);
    }

    @Override
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date) {
        return leaveRepository.findBy(byEmployeeId(employeeId).and(byDate(date)), FluentQuery.FetchableFluentQuery::first).map(MappingUtility::leaveRequestToResponse);
    }
}
