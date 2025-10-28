package com.example.perkmanager.services;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Create a new account
    public Account createAccount(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Account account = new Account(username, password);
        return accountRepository.save(account);
    }

    // Find account by username
    @Transactional(readOnly = true)
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    // Simple password check (prototype only)
    // TODO: Strengthen password check (authentication tokens or session logic when login logic added)
    @Transactional(readOnly = true)
    public boolean checkPassword(Account account, String password) {
        return account.isCorrectPassword(password);
    }

    // Link a perk to an account
    public void linkPerkToCreator(Account account, Perk perk) {
        account.addPerk(perk);
        perk.setCreator(account);
        accountRepository.save(account);
    }

    // Add a membership to account
    // TODO: Handle duplicate membership addition gracefully in frontend implementation
    public void addMembership(Account account, Membership membership) {
        // When a logged-in user searches perks, you can filter: perk.getMembership() in account.getMemberships()
        account.addMembership(membership);
        accountRepository.save(account);
    }
}
