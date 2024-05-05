package com.lql.humanresourcedemo.dto.request.search;

import java.util.List;

public record Logic(
        String column,
        String value,
        List<String> values,
        QueryOperator queryOperator,

        LogicOperator logicOperator,
        List<Logic> logics

) {
}
