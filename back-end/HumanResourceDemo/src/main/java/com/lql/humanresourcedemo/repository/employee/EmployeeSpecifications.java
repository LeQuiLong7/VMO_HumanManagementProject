package com.lql.humanresourcedemo.repository.employee;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.employee.Employee_;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {
    private EmployeeSpecifications() {
    }

    public static Specification<Employee> all() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();
    }

    public static Specification<Employee> byRole(Role role) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Employee_.ROLE), role);
    }

    public static Specification<Employee> byId(Long employeeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Employee_.ID), employeeId);
    }

    public static Specification<Employee> byPmId(Long pmId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Employee_.MANAGED_BY).get(Employee_.ID), pmId);
    }

    public static Specification<Employee> byCurrentEffortLessThanOrEqualTo(Integer currentEffort) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(Employee_.CURRENT_EFFORT), currentEffort);
    }
}
