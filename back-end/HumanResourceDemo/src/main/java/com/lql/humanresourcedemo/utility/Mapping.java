package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.model.employee.Employee;

public class Mapping {

    public static GetProfileResponse employeeToProfileResponse(Employee e) {
        return new GetProfileResponse(
                e.getId(),
                e.getEmail(),
                e.getFirstName(),
                e.getLastName(),
                e.getBirthDate(),
                e.getPhoneNumber(),
                e.getAvatarUrl(),
                e.getPersonalEmail(),
                e.getLeaveDays(),
                e.getCurrentSalary(),
                e.getRole()
        );
    }
}
