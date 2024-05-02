package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.controller.AdminController;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.paging.PagingException;
import com.lql.humanresourcedemo.security.MyAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HelperUtilityTest {
    @Test
    public void testBuildEmail() {
        String mail = HelperUtility.buildEmail("Long", "Le Qui");
        assertEquals("longlq@company.com", mail);
    }

    @Test
    void testBuildEmailWithWildcard() {
        String email = "longlq@company.com";
        assertEquals("longlq%@company.com", HelperUtility.buildEmailWithWildcard(email));
    }

    @Test
    void testEmailWithIdentityNumber() {
        String email = "longlq@company.com";
        assertEquals("longlq2@company.com", HelperUtility.emailWithIdentityNumber(email, 2));

    }

    @ParameterizedTest(name = "{0} is not a valid page number")
    @ValueSource(strings = {"abc", "-1", "4.5", ""})
    void validatePageRequest_PageNumberNotValid(String page)  {
        assertThrows(PagingException.class,
                () -> HelperUtility.validateAndBuildPageRequest(page, "1", null, null, MockClass.class),
                page + " is not a valid page number");
    }
    @ParameterizedTest(name = "{0} is not a valid page size")
    @ValueSource(strings = {"abc", "-1", "4.5", ""})
    void validatePageRequest_PageSizeNotValid(String pageSize) {
        assertThrows(PagingException.class,
                () -> HelperUtility.validateAndBuildPageRequest("0", pageSize, null, null, MockClass.class),
                pageSize + " is not a valid page number");
    }

    @ParameterizedTest(name = "{0} is not a valid order")
    @ValueSource(strings = {"abc", "-1", "4.5", ""})
    void validatePageRequest_orderNotValid(String order) {
        assertThrows(PagingException.class,
                () -> HelperUtility.validateAndBuildPageRequest("0", "1", null, List.of(order), MockClass.class),
                "%s is not a valid sort order, whether asc or desc".formatted(order));
    }
    @ParameterizedTest(name = "{0} is not a valid order")
    @ValueSource(strings = {"abc", "-1", "4.5", ""})
    void validatePageRequest_propertyNotValid(String property) {
        assertThrows(PagingException.class,
                () -> HelperUtility.validateAndBuildPageRequest("0", "1", List.of(property), null, MockClass.class),
                "%s is not a valid sort property".formatted(property));
    }


    @Test
    void validatePageRequest_success() {
       String pageNumber = "0";
       String pageSize = "10";
        List<String> properties = List.of("name", "age");
        Pageable pageRequest = HelperUtility.validateAndBuildPageRequest(pageNumber, pageSize, properties, null, MockClass.class);


        assertAll(
                () ->   assertEquals(pageNumber, pageRequest.getPageNumber()+""),
                () ->  assertEquals(pageSize, pageRequest.getPageSize()+""),
                () -> pageRequest.getSort().get().forEach(order -> {
                assertTrue(properties.contains(order.getProperty()));
                assertTrue(order.isAscending());
            })
        );
    }
}


class MockClass {
    private String name;
    private String age;
}