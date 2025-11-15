package com.example.perkmanager.controllers;

import com.example.perkmanager.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private AccountController accountController;
    private AccountService accountService;
    private Model model;

    @BeforeEach
    void setup() {
        accountService = mock(AccountService.class);
        accountController = new AccountController(accountService);
        model = mock(Model.class);
    }

    @Test
    void displaySignup() {
        String view = accountController.displaySignup(model);
        assertEquals("signup", view);
    }

    @Test
    void signup() {
        String username = "testuser";
        String password = "pass";

        String view = accountController.signup(username, password, model);

        assertEquals("redirect:/login", view);
        verify(accountService, times(1)).createAccount(username, password);
    }

    @Test
    void displayLogin() {
        String view = accountController.displayLogin(model);
        assertEquals("login", view);
    }
}
