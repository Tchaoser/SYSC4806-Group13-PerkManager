package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.repositories.AccountRepository;
import com.example.perkmanager.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class AccountControllerTest {

    private AccountController accountController;
    private AccountRepository accountRepository;
    private AccountService accountService;
    private Model model;

    @BeforeEach
    void setup() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository, new BCryptPasswordEncoder());
        accountController = new AccountController(accountService);
        model = mock(Model.class);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void displaySignup() {
        assertEquals("signup", accountController.displaySignup(model));
    }

    @Test
    void signup() {
        assertEquals("redirect:/login",  accountController.signup("testuser", "pass", model));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void displayLogin() {
        assertEquals("login", accountController.displayLogin(model));
    }

}