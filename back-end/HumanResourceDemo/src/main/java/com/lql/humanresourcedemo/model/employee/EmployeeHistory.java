package com.lql.humanresourcedemo.model.employee;

import com.lql.humanresourcedemo.model.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class EmployeeHistory extends Auditable {
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
