package com.lql.humanresourcedemo.service.search;

import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SearchResponse;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.utility.MappingUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService{
    private final EmployeeRepository employeeRepository;
    @Override
    public Page<SearchResponse> search(SearchRequest searchRequest, Pageable pageRequest) {

        Specification<Employee> specification = SpecificationService.toSpecification(searchRequest, Employee.class);
//        specification.toPredicate()
        Page<GetProfileResponse> map = employeeRepository.findBy(specification, p -> p.page(pageRequest)).map(MappingUtility::employeeToProfileResponse);

        System.out.println(map.getTotalElements());
//        System.out.println();
        return null;
    }
}
