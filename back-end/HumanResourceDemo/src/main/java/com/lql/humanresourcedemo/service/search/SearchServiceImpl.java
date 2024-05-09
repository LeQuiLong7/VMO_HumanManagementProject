package com.lql.humanresourcedemo.service.search;

import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.SearchResponse;
import com.lql.humanresourcedemo.dto.response.TechInfo;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.lql.humanresourcedemo.service.search.SpecificationService.*;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {
    private final EmployeeRepository employeeRepository;
    @Override
    @Transactional
    public Page<SearchResponse> search(SearchRequest searchRequest, Pageable pageRequest) {

        Specification<Employee> specification = toSpecification(searchRequest, Employee.class);
        Page<Employee> by = employeeRepository.findAll(specification, pageRequest);
        return by.map(
                employee ->
                        new SearchResponse(
                                MappingUtility.employeeToProfileResponse(employee),
                                employee.getTechs()
                                        .stream()
                                        .map(et -> new TechInfo(et.getId().getTechId(), et.getTech().getName(), et.getYearOfExperience())).toList(),
                                employee.getProjects().stream()
                                        .map(ep -> MappingUtility.projectToProjectResponse(ep.getProject()))
                                        .toList()
                        )
        );

    }
}