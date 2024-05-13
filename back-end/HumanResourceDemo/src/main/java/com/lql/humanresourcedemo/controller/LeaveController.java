package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
@Tag(name="4. Leave controller")
public class LeaveController {
    private final LeaveService leaveService;


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping
    public Page<LeaveResponse> getAllLeaveRequestByEmployeeId(@RequestParam(required = false, defaultValue = "0") String page,
                                                              @RequestParam(required = false, defaultValue = "10") String size,
                                                              @RequestParam(required = false, defaultValue = "createdAt") List<String> p,
                                                              @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return leaveService.getAllLeaveRequest(getCurrentEmployeeId(), validateAndBuildPageRequest(page, size, p, o, LeaveRequest.class));
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/{date}")
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(@PathVariable LocalDate date) {
        return leaveService.getLeaveRequestByDateAndEmployeeId(getCurrentEmployeeId(), date);
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveResponse createLeaveRequest(@RequestBody @Valid LeaveRequestt leaveRequestt) {
        return leaveService.createLeaveRequest(getCurrentEmployeeId(), leaveRequestt);
    }

}
