package com.lql.humanresourcedemo.model;

import jakarta.persistence.*;

@Entity
public class EmployeeHistory extends Auditable{
    @Id
    @SequenceGenerator(name = "employee_history_seq", sequenceName = "employee_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_history_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private String fieldChange;
    private String previousValue;
    private String updateValue;
}
