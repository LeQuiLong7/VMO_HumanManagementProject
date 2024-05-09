package com.lql.humanresourcedemo.service.search;

import com.lql.humanresourcedemo.dto.request.search.Logic;
import com.lql.humanresourcedemo.dto.request.search.LogicOperator;
import com.lql.humanresourcedemo.dto.request.search.QueryOperator;
import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpecificationServiceTest {


    @Test
    void toSpecification() {
        SearchRequest searchRequest = new SearchRequest(LogicOperator.AND,
                List.of(new Logic("id", "1", null, QueryOperator.EQ,null, null)));

        Specification<Employee> employeeSpecification = assertDoesNotThrow(
                () -> SpecificationService.toSpecification(searchRequest, Employee.class)
        );

    }
    @Test
    void toSpecificationNumberOfProjects() {
        SearchRequest searchRequest = new SearchRequest(LogicOperator.AND,
                List.of(new Logic("numberOfProjects", "1", null, QueryOperator.EQ,null, null)));

        Specification<Employee> employeeSpecification = assertDoesNotThrow(
                () -> SpecificationService.toSpecification(searchRequest, Employee.class)
        );

    }
}
