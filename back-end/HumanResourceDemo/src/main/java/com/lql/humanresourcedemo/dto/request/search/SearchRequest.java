package com.lql.humanresourcedemo.dto.request.search;

import java.util.List;

public record SearchRequest (
        LogicOperator logicOperator,
        List<Logic> logics
) {
}

