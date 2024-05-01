package com.lql.humanresourcedemo.model.project;

import com.lql.humanresourcedemo.model.Auditable;
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
@AllArgsConstructor
@Getter
@Setter
public class EmployeeProject extends Auditable {

    @EmbeddedId
    private EmployeeProjectId id;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EmployeeProjectId implements Serializable {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "employeeId")
        private Employee employee;
        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "projectId")
        private Project project;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EmployeeProjectId that = (EmployeeProjectId) o;

            return employee.getId().equals(that.employee.getId())
                    && project.getId().equals(that.project.getId());
        }

        @Override
        public int hashCode() {
            int result = employee != null ? employee.hashCode() : 0;
            result = 31 * result + (project != null ? project.hashCode() : 0);
            return result;
        }
    }
}
