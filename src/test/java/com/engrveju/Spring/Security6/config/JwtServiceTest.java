package com.engrveju.Spring.Security6.config;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.engrveju.Spring.Security6.config.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {JwtService.class})
@ExtendWith(SpringExtension.class)
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;

    /**
     * Method under test: {@link JwtService#validateToken(String)}
     */
    @Test
    void testValidateToken() {
        assertFalse(jwtService.validateToken("ABC123"));
        assertFalse(jwtService.validateToken("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"));
        assertFalse(jwtService.validateToken(""));
    }
}

