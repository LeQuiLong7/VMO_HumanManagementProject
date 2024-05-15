package com.lql.humanresourcedemo.service.search;


import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.search.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    Page<SearchResponse> search(SearchRequest searchRequest, Pageable pageRequest);
}
