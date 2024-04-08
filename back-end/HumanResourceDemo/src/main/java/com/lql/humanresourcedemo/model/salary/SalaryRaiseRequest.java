package com.lql.humanresourcedemo.model.salary;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
    private Double newSalary;
    private LocalDateTime approvedAt;

    @OneToOne
    @JoinColumn(name = "approvedBy")
    private Employee approvedBy;

}
