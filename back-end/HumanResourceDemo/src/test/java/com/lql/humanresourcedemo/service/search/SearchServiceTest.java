package com.lql.humanresourcedemo.service.search;


import com.lql.humanresourcedemo.dto.request.search.Logic;
import com.lql.humanresourcedemo.dto.request.search.LogicOperator;
import com.lql.humanresourcedemo.dto.request.search.QueryOperator;
import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.SearchResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.admin.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)

public class SearchServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;


    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchServiceImpl(employeeRepository);
    }


    @Test
    void searchTest() {

        Employee employee = Employee.builder().id(1L).techs(List.of()).projects(List.of()).role(Role.PM).build();

        Mockito.when(employeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee)));

        SearchRequest searchRequest = new SearchRequest(LogicOperator.AND, List.of(new Logic("a", "a", null, QueryOperator.EQ, null, null)));
        Page<SearchResponse> search = searchService.search(searchRequest, Pageable.unpaged());

        assertEquals(1, search.getTotalElements());
    }



}
