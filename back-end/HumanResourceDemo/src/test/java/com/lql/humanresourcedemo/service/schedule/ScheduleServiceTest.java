package com.lql.humanresourcedemo.service.schedule;

import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private MailService mailService;
    @Mock
    private AWSService awsService;
    private String region = "mock-region";


    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        scheduleService = new ScheduleServiceImpl(employeeRepository, attendanceRepository, leaveRepository, mailService, awsService, region);
    }


    @Test
    public void updateEmployeeLeaveDaysMonthly() {
        when(employeeRepository.increaseLeaveDaysBy1())
                .thenReturn(100);


        scheduleService.updateEmployeeLeaveDaysMonthly();

        verify(employeeRepository, times(1)).increaseLeaveDaysBy1();
    }

    @Test
    public void notifyLeaveViolation() {

        Attendance onTime = new Attendance(1L, Employee.builder().build(), LocalDate.now(), LocalTime.of(8, 20), LocalTime.of(17, 31));
        Attendance late = new Attendance(2L, Employee.builder().build(), LocalDate.now(), LocalTime.of(8, 31), LocalTime.of(17, 31));
        Attendance earlyLeave = new Attendance(3L, Employee.builder().build(), LocalDate.now(), LocalTime.of(8, 29), LocalTime.of(17, 20));
        Attendance lateAndEarlyLeave = new Attendance(4L, Employee.builder().build(), LocalDate.now(), LocalTime.of(8, 31), LocalTime.of(17, 20));
        Attendance absence = new Attendance(5L, Employee.builder().build(), LocalDate.now(), null, null);

        when(attendanceRepository.findByDate(any()))
                .thenReturn(List.of(
                        onTime,
                        late,
                        earlyLeave,
                        lateAndEarlyLeave,
                        absence
                ));
        when(employeeRepository.findById(any(), eq(OnlyPersonalEmailAndFirstName.class)))
                .thenReturn(Optional.of(Mockito.mock(OnlyPersonalEmailAndFirstName.class)));
        when(leaveRepository.findByEmployeeIdAndDate(any(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        scheduleService.notifyLeaveViolation();
        verify(mailService, times(4)).sendEmail(any(), anyString(), bodyCaptor.capture());


        assertThat(bodyCaptor.getAllValues())
                .anyMatch(body -> body.contains("ABSENCE"))
                .anyMatch(body -> body.contains("LATE_ARRIVE"))
                .anyMatch(body -> body.contains("EARLY_LEAVE"))
                .anyMatch(body -> body.contains("LATE_ARRIVE_AND_EARLY_LEAVE"));

    }


//    @Scheduled(cron = "0 9 * * 6 *") // Run at 9AM every Saturday
//    public void createEmployeeWeeklyReports() {
//        List<OnlyIdPersonalEmailAndFirstName> employees = employeeRepository.findByQuitIsFalse(OnlyIdPersonalEmailAndFirstName.class);
//
//        LocalDate startDate = LocalDate.now().minusDays(5);
//        LocalDate endDate = LocalDate.now().minusDays(1);
//        for(var i : employees) {
//
//            List<Attendance> weekAttendance = attendanceRepository.findByEmployeeIdAndDateBetween(i.id(), startDate, endDate);
//
//            String key = "report/emp_%s/weekly_report_from_%s_to_%s.csv".formatted(i.id(), startDate, endDate);
//            Map<LeaveViolationCode, Integer> agg = new HashMap<>();
//
//            try  {
//                File csvFile = createCsvFile(i.id(), weekAttendance, agg);
//
//                awsService.uploadFile(csvFile, BUCKET_NAME, key);
//                String reportUrl = awsService.getUrlForObject(BUCKET_NAME, region, key);
//
//
//                mailService.sendEmail(i.personalEmail(), "[COMPANY] - WEEKLY REPORT", buildEmployeeWeeklyReport(i.firstName(), startDate, endDate, agg, reportUrl));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

}
