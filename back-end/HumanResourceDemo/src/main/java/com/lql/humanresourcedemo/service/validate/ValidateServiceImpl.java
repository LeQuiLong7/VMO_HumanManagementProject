package com.lql.humanresourcedemo.service.validate;

import com.lql.humanresourcedemo.exception.model.paging.PagingException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ValidateServiceImpl implements ValidateService {


    private boolean isPositiveIntegerNumber(String number) {
        return Pattern.matches("\\d+", number);
    }

    private boolean isSortingOrderValid(String order) {
        try {
            Sort.Direction.valueOf(order.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public void validatePageRequest(String pageNumber, String pageSize, List<String> sortProperties, List<String> order, Class<?> clazz) {
        if (!isPositiveIntegerNumber(pageNumber)) {
            throw new PagingException("%s is not a valid page number".formatted(pageNumber));
        }
        if (!isPositiveIntegerNumber(pageSize) || Integer.parseInt(pageSize) == 0) {

            throw new PagingException("%s is not a valid page size".formatted(pageSize));
        }
        order.forEach(o -> {
            if (!isSortingOrderValid(o)) {
                throw new PagingException("%s is not a valid sort order, whether asc or desc".formatted(o));
            }
        });

        Set<String> classProperties = Arrays.stream(clazz.getDeclaredFields()).map(field -> field.getName().toLowerCase()).collect(Collectors.toSet());
        sortProperties.forEach(p -> {
            if (!classProperties.contains(p.toLowerCase())) {
                throw new PagingException("%s is not a valid sort property".formatted(p));
            }
        });


    }
}
