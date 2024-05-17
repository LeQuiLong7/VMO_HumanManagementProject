package com.lql.humanresourcedemo.service.schedule;

public interface ScheduleService {

    void updateEmployeeLeaveDaysMonthly();


    void notifyLeaveViolation();


    void createEmployeeWeeklyReports();


    void captureEffortDaily();
}
