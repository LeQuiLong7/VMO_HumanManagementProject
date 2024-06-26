package com.lql.humanresourcedemo.service.schedule;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdAndCurrentEffort;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveViolationCode;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.effort.EffortHistory;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.effort.EffortHistoryRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_ARRIVAL_TIME;
import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_LEAVE_TIME;
import static com.lql.humanresourcedemo.enumeration.LeaveViolationCode.*;
import static com.lql.humanresourcedemo.util.AWSUtil.BUCKET_NAME;
import static com.lql.humanresourcedemo.util.HelperUtil.buildEmployeeWeeklyReport;
import static com.lql.humanresourcedemo.util.HelperUtil.buildLeaveWarningMessage;

@Component
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final EffortHistoryRepository effortHistoryRepository;
    private final MailService mailService;
    private final AWSService awsService;

    private final String region;

    public ScheduleServiceImpl(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository, LeaveRepository leaveRepository, MailService mailService, EffortHistoryRepository effortHistoryRepository, AWSService awsService, @Value("${spring.cloud.aws.region.static}") String region) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
        this.effortHistoryRepository = effortHistoryRepository;
        this.mailService = mailService;
        this.awsService = awsService;
        this.region = region;
    }
//            ┌───────────── second (0-59)
//            │ ┌───────────── minute (0 - 59)
//            │ │ ┌───────────── hour (0 - 23)
//            │ │ │ ┌───────────── day of the month (1 - 31)
//            │ │ │ │ ┌───────────── month (1 - 12) (or JAN-DEC)
//            │ │ │ │ │ ┌───────────── day of the week (0 - 7)
//            │ │ │ │ │ │          (0 or 7 is Sunday, or MON-SUN)
//            │ │ │ │ │ │
//            * * * * * *
    @Scheduled(cron = "0 0 0 1 * *") // Run at midnight on the first day of each month
    // add 1 leave day to all employees each month
    public void updateEmployeeLeaveDaysMonthly() {
        int i = employeeRepository.increaseLeaveDaysBy1();
        log.info("updated leave days for {} employees, date: {}", i, LocalDate.now());
    }


    @Scheduled(cron = "0 0 9 * * 2-6") // Run at 9AM Tuesday to Saturday
    // mail notification for employees if their previous day's attendance status is not ON_TIME
    public void notifyLeaveViolation() {

        List<Attendance> attendances = attendanceRepository.findByDate(LocalDate.now().minusDays(1));

        attendances.forEach(attendance -> {
            LeaveViolationCode violationCode = of(attendance);
            if (!violationCode.equals(ON_TIME)) {
                OnlyPersonalEmailAndFirstName personalEmailAndFirstName = employeeRepository.findById(attendance.getEmployee().getId(), OnlyPersonalEmailAndFirstName.class)
                        .orElseThrow(() -> new EmployeeException("Could not find employee " + attendance.getEmployee().getId()));

                mailService.sendEmail(personalEmailAndFirstName.personalEmail(), "[COMPANY] - Timekeeping explanation", buildLeaveWarningMessage(personalEmailAndFirstName.firstName(), attendance.getDate(), violationCode));
            }
        });
    }

    @Override
    @Scheduled(cron = "0 0 9 * * 6") // Run at 9AM every Saturday
    // create weekly attendance reports, store on s3 and mail notification for attendance status of the week
    public void createEmployeeWeeklyReports() {
        // get all employees that have not quit
        List<OnlyIdPersonalEmailAndFirstName> employees = employeeRepository.findByQuitIsFalse(OnlyIdPersonalEmailAndFirstName.class);
        // run on Saturday then minus 5 will be Monday, minus 1 will be Friday
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().minusDays(1);
        for (var i : employees) {
            // get all attendance history in the week of employee
            List<Attendance> weekAttendance = attendanceRepository.findByEmployeeIdAndDateBetween(i.id(), startDate, endDate);
            // report key on s3: report/emp_{employeeId}/weekly_report_from_{startDate}_to_{endDate}.csv
            String key = "report/emp_%s/weekly_report_from_%s_to_%s.csv".formatted(i.id(), startDate, endDate);

            // map for violation count
            // example: ON_TIME: 4
            //          LATE_ARRIVE: 2
            Map<LeaveViolationCode, Integer> agg = new EnumMap<>(LeaveViolationCode.class);

            try {
                File csvFile = createCsvFile(i.id(), weekAttendance, agg);
                String reportUrl = awsService.uploadReport(csvFile, key);
                // send the violation count to employee's email along with the link to the report on S3
                mailService.sendEmail(i.personalEmail(), "[COMPANY] - WEEKLY REPORT", buildEmployeeWeeklyReport(i.firstName(), startDate, endDate, agg, reportUrl));

            } catch (IOException e) {
                log.warn("Error creating report file");
            }

        }
    }

    @Override
    @Scheduled(cron = "0 0 17 * * 1-5") // Run at 5PM Monday to Friday
    // capture current effort of all employees
    public void captureEffortDaily() {
        LocalDate today = LocalDate.now();

        employeeRepository.findByQuitIsFalse(OnlyIdAndCurrentEffort.class)
                .forEach(emp -> effortHistoryRepository.save(new EffortHistory(emp.id(), today, emp.currentEffort())));
        log.info("Capture employee effort successfully - Date: {}", today);
    }

    private File createCsvFile(Long employeeId, List<Attendance> attendanceList, Map<LeaveViolationCode, Integer> agg) throws IOException {
        File file = File.createTempFile("temp", ".csv");
        try (FileWriter writer = new FileWriter(file)) {
            // csv file columns: Employee ID, Day, Violation Type,Time In,Time Out
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
        // time and and time out are null mean they are absence that day
        // check whether they have any accepted leave request on that day or not
        if (attendance.getTimeIn() == null && attendance.getTimeOut() == null) {
            Optional<LeaveRequest> leaveRequest = leaveRepository.findByEmployeeIdAndDate(attendance.getEmployee().getId(), attendance.getDate());
            if (leaveRequest.isEmpty() || !leaveRequest.get().getStatus().equals(LeaveStatus.ACCEPTED)) {
                return ABSENCE;
            }
            return ON_TIME;
        } else if (attendance.getTimeIn().isAfter(COMPANY_ARRIVAL_TIME)) {
            if (attendance.getTimeOut().isBefore(COMPANY_LEAVE_TIME))
                return LATE_ARRIVE_AND_EARLY_LEAVE;
            return LATE_ARRIVE;
        } else if (attendance.getTimeOut().isBefore(COMPANY_LEAVE_TIME)) {
            return EARLY_LEAVE;
        }
        return ON_TIME;
    }
}
