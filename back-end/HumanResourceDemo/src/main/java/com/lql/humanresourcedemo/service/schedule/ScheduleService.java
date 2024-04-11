package com.lql.humanresourcedemo.service.schedule;

import com.lql.humanresourcedemo.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final EmployeeService employeeService;

    @Scheduled(cron = "0 0 0 1 * *") // Run at midnight on the first day of each month
    public void executeMonthlyTask() {
        int i = employeeService.updateLeaveDays();

        log.info("updated leave days for {} employees, date: {}", i, LocalDate.now());


    }
}
