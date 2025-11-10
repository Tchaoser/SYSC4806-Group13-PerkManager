package com.example.perkmanager.config;

import com.example.perkmanager.security.AccountUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private AccountUserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        securityConfig = new SecurityConfig();
        userDetailsService = mock(AccountUserDetailsService.class);

    }

    @Test
    void passwordEncoder(){
        Object encoder1 = securityConfig.passwordEncoder();
        assertInstanceOf(BCryptPasswordEncoder.class, encoder1);
        String encodedPassword = ((BCryptPasswordEncoder)encoder1).encode("password");
        BCryptPasswordEncoder encoder2 = (BCryptPasswordEncoder) securityConfig.passwordEncoder();
        assertTrue(encoder2.matches("password", encodedPassword));
    }

    @Test
    void authenticationProvider() {
        String testuser = "testuser";
        String testpass = "pass";

        DaoAuthenticationProvider authenticationProvider = securityConfig.authenticationProvider(userDetailsService, securityConfig.passwordEncoder());

        UserDetails user = User.withUsername(testuser)
                .password(securityConfig.passwordEncoder().encode(testpass))
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername(testuser)).thenReturn(user);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(testuser, testpass);

        Authentication result = authenticationProvider.authenticate(authToken);

        assertTrue(result.isAuthenticated());
        assertEquals(testuser, result.getName());
    }


}
