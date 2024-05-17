package com.lql.humanresourcedemo.repository.effort;

import com.lql.humanresourcedemo.model.effort.EffortHistory;
import com.lql.humanresourcedemo.model.effort.EffortHistory_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EffortHistorySpecifications {
    private EffortHistorySpecifications() {
    }

    public static Specification<EffortHistory> byDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(EffortHistory_.ID).get("date"), startDate, endDate);
    }
    public static Specification<EffortHistory> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(EffortHistory_.ID).get("employeeId"), employeeId);
    }
}
