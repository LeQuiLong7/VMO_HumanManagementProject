package com.lql.humanresourcedemo.service.tech;

import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.model.tech.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TechService {
    Page<Tech> getAllTech(Pageable pageRequest);

    TechStackResponse getTechStackByEmployeeId(Long empId);
    TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request);


}
