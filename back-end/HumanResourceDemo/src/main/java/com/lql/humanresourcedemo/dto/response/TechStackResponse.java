package com.lql.humanresourcedemo.dto.response;

import java.util.List;

public record TechStackResponse(List<TechInfo> techInfo) {

    public static record TechInfo(Long techId, String techName, Double yearOfExperience){
    }
}


