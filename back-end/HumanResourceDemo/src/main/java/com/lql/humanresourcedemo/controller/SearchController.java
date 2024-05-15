package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import com.lql.humanresourcedemo.dto.response.search.SearchResponse;
import com.lql.humanresourcedemo.service.search.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name="8. Search controller")
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/employees")
    public Page<SearchResponse> getAllEmployee(Pageable page, @RequestBody @Valid SearchRequest searchRequest) {
        return searchService.search(searchRequest, page);
    }
}
