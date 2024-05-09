package com.lql.humanresourcedemo.model.tech;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class EmployeeTech {

    @EmbeddedId
    private EmployeeTechId id = new EmployeeTechId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("techId")
    private Tech tech;


    private Double yearOfExperience;


    public EmployeeTech(Employee employee, Tech tech, Double yearOfExperience) {
        this.employee = employee;
        this.tech = tech;
        this.yearOfExperience = yearOfExperience;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EmployeeTechId implements Serializable {

        private Long employeeId;
        private Long techId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EmployeeTechId that = (EmployeeTechId) o;

            if (!Objects.equals(employeeId, that.employeeId)) return false;
            return Objects.equals(techId, that.techId);
        }

        @Override
        public int hashCode() {
            int result = employeeId != null ? employeeId.hashCode() : 0;
            result = 31 * result + (techId != null ? techId.hashCode() : 0);
            return result;
        }
    }

}
