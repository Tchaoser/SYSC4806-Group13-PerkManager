package com.example.perkmanager.services;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository, new BCryptPasswordEncoder());

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createAccount_shouldSaveAccount() {
        when(accountRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        Account account = accountService.createAccount("user1", "password");

        assertEquals("user1", account.getUsername());
        verify(accountRepository).save(account);
    }

    @Test
    void createAccount_existingUsername_shouldThrow() {
        Account existing = new Account();
        existing.setUsername("user1");
        existing.setPassword("pass");

        when(accountRepository.findByUsername("user1"))
                .thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount("user1", "pass"));
    }

    @Test
    void linkPerkToCreator_shouldLinkPerkAndSaveAccount() {
        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("pass");
        Perk perk = new Perk();
        perk.setBenefit("10% off");

        when(accountRepository.save(account)).thenReturn(account);

        accountService.linkPerkToCreator(account, perk);

        assertTrue(account.getPerks().contains(perk));
        assertEquals(account, perk.getCreator());
        verify(accountRepository).save(account);
    }

    @Test
    void addMembership(){
        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("pass");
        Membership membership = new Membership("Credit Card", "RBC", "RBC Rewards Member");

        when(accountRepository.save(account)).thenReturn(account);

        accountService.addMembership(account, membership);

        assertTrue(account.getMemberships().contains(membership));
    }

    @Test
    void removeMembership(){
        Account account = new Account();
        account.setUsername("user1");
        account.setPassword("pass");
        Membership membership = new Membership("Credit Card", "RBC", "RBC Rewards Member");

        when(accountRepository.save(account)).thenReturn(account);

        accountService.addMembership(account, membership);

        assertTrue(account.getMemberships().contains(membership));

        accountService.removeMembership(account, membership);
        assertFalse(account.getMemberships().contains(membership));
    }
}
