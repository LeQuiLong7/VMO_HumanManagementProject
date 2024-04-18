package com.lql.humanresourcedemo.dto.model.tech;

import jakarta.validation.constraints.Min;

public record TechStack(Long techId,
                        @Min(0) Double yearOfExperience) {
}
