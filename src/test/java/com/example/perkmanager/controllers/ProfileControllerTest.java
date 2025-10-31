package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.services.AccountService;
import com.example.perkmanager.services.MembershipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ProfileController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(ProfileControllerTest.TestSecurityConfig.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private MembershipService membershipService;

    // ✅ Injects a fake CSRF token to prevent Thymeleaf "_csrf.token" errors
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean(name = "_csrf")
        public CsrfToken csrfToken() {
            return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "test-token");
        }
    }


    @Test
    @DisplayName("POST /profile/memberships/add adds membership and returns JSON payload")
    void postAddMembership() throws Exception {
        Account acc = new Account();
        acc.setUsername("Sap");
        when(accountService.findByUsername("Sap")).thenReturn(Optional.of(acc));

        Membership m = new Membership();
        m.setId(Long.valueOf(1L));
        when(membershipService.findById(Long.valueOf(1L))).thenReturn(Optional.of(m));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Sap", "pass")
        );

        mockMvc.perform(post("/profile/memberships/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("membershipId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        Mockito.verify(accountService).addMembership(eq(acc), eq(m));
    }

    @Test
    @DisplayName("POST /profile/memberships/remove removes membership and returns JSON payload")
    void postRemoveMembership() throws Exception {
        Account acc = new Account();
        acc.setUsername("Sap");
        when(accountService.findByUsername("Sap")).thenReturn(Optional.of(acc));

        Membership m = new Membership();
        m.setId(Long.valueOf(2L));
        when(membershipService.findById(Long.valueOf(2L))).thenReturn(Optional.of(m));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Sap", "pass")
        );

        mockMvc.perform(post("/profile/memberships/remove")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("membershipId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        Mockito.verify(accountService).removeMembership(eq(acc), eq(m));
    }
}
