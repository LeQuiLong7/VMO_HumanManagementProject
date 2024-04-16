package com.lql.humanresourcedemo.service.validate;

import java.util.List;

public interface ValidateService {

    void validatePageRequest(String pageNumber, String pageSize, List<String> property, List<String> order, Class<?> clazz);
}
