package com.lql.humanresourcedemo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Attendance extends Auditable{
    @Id
    @SequenceGenerator(name = "attendance_id_seq", sequenceName = "attendance_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_id_seq")
    private Long id;
    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private LocalDate date;
    private LocalTime timeIn;
    private LocalTime timeOut;
}
