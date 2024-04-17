package com.lql.humanresourcedemo.utility;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testBuildPageRequest() {
        int pageNumber = 1;
        int pageSize = 1;
        List<String> properties = List.of("name", "age");
        Pageable pageRequest = HelperUtility.buildPageRequest(pageNumber, pageSize, properties, null, MockClass.class);


        assertAll(() -> {
            assertEquals(pageNumber, pageRequest.getPageNumber());
            assertEquals(pageSize, pageRequest.getPageSize());
            pageRequest.getSort().get().forEach(order -> {
                assertTrue(properties.contains(order.getProperty()));
                assertTrue(order.isAscending());
            });
        });
    }
}


class MockClass {
    private String name;
    private String age;
}