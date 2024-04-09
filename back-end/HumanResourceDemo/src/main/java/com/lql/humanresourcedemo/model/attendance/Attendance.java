package com.lql.humanresourcedemo.model.attendance;

import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Attendance extends Auditable {
    @Id
    @SequenceGenerator(name = "attendance_id_seq", sequenceName = "attendance_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_id_seq")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
}
