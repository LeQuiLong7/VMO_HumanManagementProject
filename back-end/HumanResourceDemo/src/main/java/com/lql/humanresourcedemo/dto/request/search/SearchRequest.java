package com.lql.humanresourcedemo.dto.request.search;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SearchRequest (
        @NotNull(message = "Logic operator must not be null")
        LogicOperator logicOperator,
        @NotNull(message = "Logics must not be null")
        List<Logic> logics
) {
}

