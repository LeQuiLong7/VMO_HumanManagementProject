package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SearchResponse;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.service.search.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name="8. Search controller")
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/employees")
    public Page<SearchResponse> getAllEmployee(@RequestParam(required = false, defaultValue = "0") String page,
                                               @RequestParam(required = false, defaultValue = "10") String size,
                                               @RequestParam(required = false, defaultValue = "id") List<String> p,
                                               @RequestParam(required = false, defaultValue = "asc") List<String> o,
                                               @RequestBody SearchRequest searchRequest) {


        Pageable pageRequest = validateAndBuildPageRequest(page, size, p, o, Employee.class);

        return searchService.search(searchRequest, pageRequest);
    }
}
