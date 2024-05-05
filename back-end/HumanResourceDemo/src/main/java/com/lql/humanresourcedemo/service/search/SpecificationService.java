package com.lql.humanresourcedemo.service.search;

import com.lql.humanresourcedemo.dto.request.search.Logic;
import com.lql.humanresourcedemo.dto.request.search.LogicOperator;
import com.lql.humanresourcedemo.dto.request.search.SearchRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SpecificationService {

    public static <T> Specification<T> toSpecification(SearchRequest searchRequest, Class<T> clazz) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            processLogic(searchRequest.logics(), root, query, criteriaBuilder, predicates);
            return combineLogicByOperator(criteriaBuilder, searchRequest.logicOperator(), predicates);
        };
    }

    private static void processLogic(
            List<Logic> logics,
            Root<?> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            List<Predicate> predicates
    ) {

        for (Logic logic : logics) {
            if(logic.queryOperator() != null)
                predicates.add(createPredicate(logic, root, criteriaBuilder));

            if (logic.logicOperator() != null && logic.logics() != null && !logic.logics().isEmpty()) {

                List<Predicate> nestedPredicates = new ArrayList<>();
                processLogic(logic.logics(), root, query, criteriaBuilder, nestedPredicates);

                predicates.add(combineLogicByOperator(criteriaBuilder, logic.logicOperator(), nestedPredicates));
            }
        }
    }

    private static Predicate combineLogicByOperator(CriteriaBuilder criteriaBuilder, LogicOperator logicOperator, List<Predicate> predicates) {
        return switch (logicOperator) {
            case AND -> criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            case OR -> criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate createPredicate(Logic logic, Root<?> root, CriteriaBuilder criteriaBuilder) {
        String[] splitPath = logic.column().split("\\.");

        Path<String> objectPath = root.get(splitPath[0]);

        if(splitPath.length > 1) {
            for(int i = 1; i < splitPath.length; i++) {
                objectPath = objectPath.get(splitPath[i]);
            }
        }



        return switch (logic.queryOperator()) {
            case EQ -> criteriaBuilder.equal(objectPath, logic.value());
            case GT -> criteriaBuilder.greaterThan(objectPath, logic.value());
            case LT -> criteriaBuilder.lessThan(objectPath, logic.value());
            case GTE -> criteriaBuilder.greaterThanOrEqualTo(objectPath, logic.value());
            case LTE -> criteriaBuilder.lessThanOrEqualTo(objectPath, logic.value());
            case IN -> {
                CriteriaBuilder.In<String> in = criteriaBuilder.in(objectPath);
                logic.values().forEach(in::value);
                yield in;
            }
            case BT -> criteriaBuilder.between(objectPath, logic.values().get(0), logic.values().get(logic.values().size() - 1));
            case NEQ -> criteriaBuilder.notEqual(objectPath, logic.value());
        };
    }
}
