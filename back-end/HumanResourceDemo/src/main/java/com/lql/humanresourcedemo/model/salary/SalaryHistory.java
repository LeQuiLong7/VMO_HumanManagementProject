package com.lql.humanresourcedemo.model.salary;

import com.lql.humanresourcedemo.model.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SalaryHistory extends Auditable {
    @Id
    @OneToOne
    @JoinColumn(name = "salaryRaiseRequestId")
    private SalaryRaiseRequest salaryRaiseRequest;

    private Double newSalary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SalaryHistory that = (SalaryHistory) o;
        return Objects.equals(salaryRaiseRequest.getId(), that.salaryRaiseRequest.getId());
    }

    @Override
    public int hashCode() {
        int result = salaryRaiseRequest != null ? salaryRaiseRequest.hashCode() : 0;
        result = 31 * result + (newSalary != null ? newSalary.hashCode() : 0);
        return result;
    }
}
