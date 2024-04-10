package com.lql.humanresourcedemo.model.salary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalaryRaiseRequest extends Auditable {
    @JsonIgnore
    @Id
    @SequenceGenerator(name = "salary_raise_req_id_seq", sequenceName = "salary_raise_req_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_raise_req_id_seq")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private Double currentSalary;
    private Double expectedSalary;
    private String description;
    @Enumerated(EnumType.STRING)
    private SalaryRaiseRequestStatus status;
    private Double newSalary;
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "approvedBy")
    private Employee approvedBy;

}
