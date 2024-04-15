package com.lql.humanresourcedemo.service.schedule;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveViolationCode;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.AttendanceRepository;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_ARRIVAL_TIME;
import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_LEAVE_TIME;
import static com.lql.humanresourcedemo.enumeration.LeaveViolationCode.*;
import static com.lql.humanresourcedemo.utility.AWSUtility.*;
import static com.lql.humanresourcedemo.utility.HelperUtility.*;
import static com.lql.humanresourcedemo.utility.HelperUtility.buildLeaveWarningMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final MailService mailService;
    private final AWSService awsService;


    @Value("${spring.cloud.aws.region.static}")
    private String region;


    @Scheduled(cron = "0 0 0 1 * *") // Run at midnight on the first day of each month
    public void updateLeaveDaysMonthly() {
        int i = employeeRepository.increaseLeaveDaysBy1();
        log.info("updated leave days for {} employees, date: {}", i, LocalDate.now());
    }


    @Scheduled(cron = "0 9 * * 2-6 *") // Run at 9AM Tuesday to Saturday
    public void notifyLeaveViolation() {

        List<Attendance> attendances = attendanceRepository.findByDate(LocalDate.now().minusDays(1));

        attendances.forEach(attendance ->  {
            LeaveViolationCode violationCode = of(attendance);
            if(!violationCode.equals(ON_TIME)) {
                OnlyPersonalEmailAndFirstName personalEmailAndFirstName = employeeRepository.findById(attendance.getEmployee().getId(), OnlyPersonalEmailAndFirstName.class)
                        .orElseThrow(() -> new EmployeeException("Could not find employee " + attendance.getEmployee().getId()));

                mailService.sendEmail(personalEmailAndFirstName.personalEmail(), "[COMPANY] - Timekeeping explanation", buildLeaveWarningMessage(personalEmailAndFirstName.firstName(), attendance.getDate(), violationCode));
            }
        });
    }

    @Scheduled(cron = "0 9 * * 6 *") // Run at 9AM every Saturday
    public void employeeWeeklyReports() {
        List<OnlyIdPersonalEmailAndFirstName> employees = employeeRepository.findByQuitIsFalse();

        for(var i : employees) {
            LocalDate startDate = LocalDate.now().minusDays(5);
            LocalDate endDate = LocalDate.now().minusDays(1);

            List<Attendance> weekAttendance = attendanceRepository.findByEmployeeIdAndDateBetween(i.id(), startDate, endDate);

            String key = "report/emp_%s/weekly_report_from_%s_to_%s.csv".formatted(i.id(), startDate, endDate);
            Map<LeaveViolationCode, Integer> agg = new HashMap<>();

            try  {
                 File csvFile = createCsvFile(i.id(), weekAttendance, agg);

                awsService.uploadFile(csvFile, BUCKET_NAME, key);
                String reportUrl = awsService.getUrlForObject(BUCKET_NAME, region, key);


                mailService.sendEmail(i.personalEmail(), "[COMPANY] - WEEKLY REPORT", buildEmployeeWeeklyReport(i.firstName(), startDate, endDate, agg, reportUrl));

           } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }




    private File createCsvFile(Long employeeId, List<Attendance> attendanceList, Map<LeaveViolationCode, Integer> agg) throws IOException {
        File file = File.createTempFile("temp", ".csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Employee ID,Day,Violation Type,Time In,Time Out\n");
            for (Attendance attendance : attendanceList) {
                writer.append(String.valueOf(employeeId)).append(",");
                writer.append(String.valueOf(attendance.getDate())).append(",");

                LeaveViolationCode violationCode = of(attendance);
                agg.put(violationCode, agg.getOrDefault(violationCode, 0) + 1);

                writer.append(String.valueOf(violationCode)).append(",");
                writer.append(String.valueOf(attendance.getTimeIn())).append(",");
                writer.append(String.valueOf(attendance.getTimeOut())).append("\n");
            }
        }
        return file;
    }


    private LeaveViolationCode of(Attendance attendance) {
        if(attendance.getTimeIn() == null && attendance.getTimeOut() == null){
            LeaveRequest leaveRequest = leaveRepository.findByEmployeeIdAndDate(attendance.getEmployee().getId(), attendance.getDate());
            if(leaveRequest == null || !leaveRequest.getStatus().equals(LeaveStatus.ACCEPTED)) {
                return ABSENCE;
            }
            return ON_TIME;
        }
        else if(attendance.getTimeIn().isAfter(COMPANY_ARRIVAL_TIME)) {
            if( attendance.getTimeOut().isBefore(COMPANY_LEAVE_TIME))
                return LATE_ARRIVE_AND_EARLY_LEAVE;
            return LATE_ARRIVE;
        } else if (attendance.getTimeOut().isBefore(COMPANY_LEAVE_TIME)) {
            return EARLY_LEAVE;
        }
        return ON_TIME;
    }
}
