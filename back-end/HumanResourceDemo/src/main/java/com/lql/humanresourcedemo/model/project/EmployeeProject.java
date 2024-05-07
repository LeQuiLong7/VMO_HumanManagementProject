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
@Getter
@Setter
public class EmployeeProject extends Auditable {

    @EmbeddedId
    private EmployeeProjectId id = new EmployeeProjectId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    private Project project;


    public EmployeeProject(Employee employee, Project project) {
        this.employee = employee;
        this.project = project;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EmployeeProjectId implements Serializable {

        private Long employeeId;
        private Long projectId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EmployeeProjectId that = (EmployeeProjectId) o;

            if (!Objects.equals(employeeId, that.employeeId)) return false;
            return Objects.equals(projectId, that.projectId);
        }

        @Override
        public int hashCode() {
            int result = employeeId != null ? employeeId.hashCode() : 0;
            result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
            return result;
        }
    }

}
