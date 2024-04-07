package com.lql.humanresourcedemo.model.salary;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SalaryRaiseRequest that = (SalaryRaiseRequest) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        result = 31 * result + (currentSalary != null ? currentSalary.hashCode() : 0);
        result = 31 * result + (expectedSalary != null ? expectedSalary.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
