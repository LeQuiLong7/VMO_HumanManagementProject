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

@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService{
    private final EmployeeRepository employeeRepository;
    @Override
    @Transactional
    public Page<SearchResponse> search(SearchRequest searchRequest, Pageable pageRequest) {

        Specification<Employee> specification = SpecificationService.toSpecification(searchRequest, Employee.class);
        return employeeRepository.findBy(specification, p -> p.project("techs.tech").page(pageRequest)).map(
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





//
////        System.out.println(map.getTotalElements());
//
//
//        Specification<Employee> specification2 = (root, query, criteriaBuilder) -> {
//
//            query.select(root.get("firstName"));
//            return criteriaBuilder.equal(root.get("id"), "1");
//        };

//
//
//        Specification<String> specification3 = (root, query, criteriaBuilder) -> {
//
//            query.select(root.get("firstName"));
//            return criteriaBuilder.equal(root.get("id"), "1");
//        };
//
//        List<Employee> all = employeeRepository.findAll(specification2);
//
//
//        System.out.println(all.size());


//        System.out.println();
//        return null;
    }
}

interface FistNameOnly {
    String getFirstName();
}