package com.example.clinic.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityHelperClassTest {

    @Test
    void getJwtValidationKey() {
        assertNotNull(new SecurityHelperClass().getJwtValidationKey());
    }
}