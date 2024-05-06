package com.lql.humanresourcedemo.repository.employee;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EmployeeSpecifications {
    public static Specification<Employee> byRole(Role role) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), role);
    }
    public static Specification<Employee> byId(Long employeeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), employeeId);
    }
    public static Specification<Employee> byPmId(Long pmId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("managedBy").get("id"), pmId);
    }
}
