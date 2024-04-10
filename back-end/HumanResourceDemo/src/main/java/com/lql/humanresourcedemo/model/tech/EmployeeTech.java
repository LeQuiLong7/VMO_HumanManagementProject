package com.lql.humanresourcedemo.model.tech;

import com.lql.humanresourcedemo.dto.model.EmployeeTechDTO;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.tech.Tech;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeTech {

    @EmbeddedId
    private EmployeeTechId id;
    private Double yearOfExperience;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EmployeeTechId implements Serializable {


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "employeeId")
        private Employee employee;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "techId")
        private Tech tech;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EmployeeTechId that = (EmployeeTechId) o;


            return employee.getId().equals(that.employee.getId())
                    && tech.getId().equals(that.tech.getId());
        }

        @Override
        public int hashCode() {
            int result = employee != null ? employee.hashCode() : 0;
            result = 31 * result + (tech != null ? tech.hashCode() : 0);
            return result;
        }
    }
}

