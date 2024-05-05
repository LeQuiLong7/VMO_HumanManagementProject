package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;

public record TechInfo(Long techId, String techName, Double yearOfExperience) {

    public static TechInfo of(EmployeeTech et) {
        return new TechInfo(et.getTech().getId(), et.getTech().getName(), et.getYearOfExperience());
    }
}
