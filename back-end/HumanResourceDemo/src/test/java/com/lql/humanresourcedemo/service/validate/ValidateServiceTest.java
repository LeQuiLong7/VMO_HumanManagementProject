package com.lql.humanresourcedemo.service.validate;

import com.lql.humanresourcedemo.exception.model.paging.PagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ValidateServiceTest {
    private final Class<MockClass> mockClass = MockClass.class;
    private final ValidateService validateService = new ValidateServiceImpl();


    @ParameterizedTest(name = "{0} is  a valid page number")
    @ValueSource(strings = {"0", "4", "20"})
    void testValidPageNumber(String pageNumber) {
        assertDoesNotThrow(() ->
                validateService.validatePageRequest(pageNumber, "10", null, null, mockClass));
    }


    @ParameterizedTest(name = "{0} is not a valid page number")
    @ValueSource(strings = {"-1", "abc", "", "1.5", "-5.6"})
    void testInvalidPageNumber(String pageNumber) {
        PagingException exception = assertThrows(PagingException.class, () ->
                validateService.validatePageRequest(pageNumber, "10", null, null, null));
        assertEquals("%s is not a valid page number".formatted(pageNumber), exception.getMessage());
    }

    @ParameterizedTest(name = "{0} is a valid page size")
    @ValueSource(strings = {"1", "5", "10", "30"})
    void testValidPageSize(String pageSize) {
        assertDoesNotThrow(() ->
                validateService.validatePageRequest("1", pageSize, null, null, mockClass));
    }


    @ParameterizedTest(name = "{0} is not a valid page size")
    @ValueSource(strings = {"0", "-1", "abc", "", "1.5", "-5.6"})
    void testInvalidPageSize(String pageSize) {
        PagingException exception = assertThrows(PagingException.class, () ->
                validateService.validatePageRequest("1", pageSize, null, null, null));
        assertEquals("%s is not a valid page size".formatted(pageSize), exception.getMessage());
    }

    @ParameterizedTest(name = "{0} is a valid order")
    @ValueSource(strings = {"asc", "aSc", "AsC", "desc", "Desc", "DeSc"})
    void testValidSortingOrder(String order) {
        List<String> orders = List.of(order);
        assertDoesNotThrow(() ->
                validateService.validatePageRequest("1", "10", null, orders, mockClass));
    }

    @ParameterizedTest(name = "{0} is not a valid order")
    @ValueSource(strings = {"", "abc", "-1", "5", "zz"})
    void testInvalidSortingOrder(String order) {
        List<String> orders = List.of(order);
        PagingException exception = assertThrows(PagingException.class, () ->
                validateService.validatePageRequest("1", "10", null, orders, null));
        assertEquals("%s is not a valid sort order, whether asc or desc".formatted(order), exception.getMessage());
    }

    @ParameterizedTest(name = "{0} is a valid sort property")
    @ValueSource(strings = {"name", "Name", "NaMe", "age", "AGE", "AgE"})
    void testValidSortProperty(String property) {
        List<String> sortProperties = List.of(property);
        assertDoesNotThrow(() ->
                validateService.validatePageRequest("1", "10", sortProperties, null, mockClass));
    }

    @ParameterizedTest(name = "{0} is not a valid sort property")
    @ValueSource(strings = {"", "abc", "-1", "5", "zz"})
    void testInvalidSortProperty(String property) {
        List<String> sortProperties = List.of(property);

        PagingException exception = assertThrows(PagingException.class, () ->
                validateService.validatePageRequest("1", "10", sortProperties, null, mockClass));
        assertEquals("%s is not a valid sort property".formatted(property), exception.getMessage());
    }

    @Test
    void testNullOrderAndSortProperties() {
        assertDoesNotThrow(() -> validateService.validatePageRequest("1", "10", null, null, mockClass));
    }


    @ParameterizedTest
    @MethodSource("generateValidData")
    void testValidPageRequest(String pageNumber, String pageSize, List<String> properties, List<String> orders) {
        assertDoesNotThrow(() -> validateService.validatePageRequest(pageNumber, pageSize, properties, orders, mockClass));
    }

    static List<Arguments> generateValidData() {
        return List.of(
                Arguments.of("1", "1", List.of("name"), List.of("asc")),
                Arguments.of("1", "1", List.of("name", "age"), List.of("asc", "Desc")),
                Arguments.of("0", "10", List.of("name", "age"), List.of("asc")),
                Arguments.of("1", "5", List.of("age"), null),
                Arguments.of("1", "5", null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("generateInvalidData")
    void testInValidPageRequest(String pageNumber, String pageSize, List<String> properties, List<String> orders) {
        assertThrows(PagingException.class, () -> validateService.validatePageRequest(pageNumber, pageSize, properties, orders, mockClass));
    }

    static List<Arguments> generateInvalidData() {
        return List.of(
                Arguments.of("1", "0",null, null),
                Arguments.of("-1", "",null, null),
                Arguments.of("a", "a",null, null),
                Arguments.of("1", "1", List.of("abc"), List.of("asc", "Desc")),
                Arguments.of("0", "10", List.of("name", "age"), List.of("zzz")),
                Arguments.of("1", "5", List.of("name", "zzz"), null)
        );
    }


    static class MockClass {
        private String name;
        private String age;
    }

}

