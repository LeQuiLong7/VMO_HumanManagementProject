package com.lql.humanresourcedemo.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelperUtilityTest {
    @Test
    public void createMailBaseOnNameTest() {
        String mail = HelperUtility.buildEmail("Long", "Le Qui");


        assertEquals("longlq@company.com", mail);
    }

    @Test
    void buildEmailWithWildcard() {
        String email = "longlq@company.com";

        assertEquals("longlq%@company.com", HelperUtility.buildEmailWithWildcard(email));
    }

    @Test
    void emailWithIdentityNumber() {
        String email = "longlq@company.com";
        assertEquals("longlq2@company.com", HelperUtility.emailWithIdentityNumber(email, 2));

    }
}