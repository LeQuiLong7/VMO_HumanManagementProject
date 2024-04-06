package com.lql.humanresourcedemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class SalaryHistory extends Auditable{
    @Id
    @OneToOne
    @JoinColumn(name = "salaryRaiseRequestId")
    private SalaryRaiseRequest salaryRaiseRequest;

    private Double newSalary;


}
