package com.lql.humanresourcedemo.repository.employee;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.jpa.domain.Specification;

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
    public static Specification<Employee> byCurrentEffortLessThanOrEqualTo(Integer currentEffort) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("currentEffort"), currentEffort);
    }
    public static Specification<Employee> byPersonalEmail(String personalEmail) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("personalEmail"), personalEmail);
    }
}
