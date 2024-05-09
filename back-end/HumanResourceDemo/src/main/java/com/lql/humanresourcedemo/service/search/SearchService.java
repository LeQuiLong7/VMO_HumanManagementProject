package com.lql.humanresourcedemo.service.search;


import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SearchResponse;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SearchService {
    Page<SearchResponse> search(SearchRequest searchRequest, Pageable pageRequest);
}
