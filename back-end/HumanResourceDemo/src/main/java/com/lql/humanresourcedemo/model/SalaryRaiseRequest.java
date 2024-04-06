package com.lql.humanresourcedemo.model;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import jakarta.persistence.*;

@Entity
public class SalaryRaiseRequest extends Auditable {
    @Id
    @SequenceGenerator(name = "salary_raise_req_id_seq", sequenceName = "salary_raise_req_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_raise_req_id_seq")
    private Long id;
    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private Double currentSalary;
    private Double expectedSalary;
    private String description;
    @Enumerated(EnumType.STRING)
    private SalaryRaiseRequestStatus status;
}
