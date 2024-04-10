package com.lql.humanresourcedemo.dto.response;

import java.util.List;

public record TechStackResponse(Long employeeId, List<TechInfo> techInfo) {

    public  record TechInfo(Long techId, String techName, Double yearOfExperience){
    }
}


