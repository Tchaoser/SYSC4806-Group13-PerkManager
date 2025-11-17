package com.example.perkmanager.services;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.repositories.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.perkmanager.model.Perk;

import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new account
    public Account createAccount(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    // Find account by username
    @Transactional(readOnly = true)
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
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

    //remove an account, required for profile to work
    public void removeMembership(Account account, Membership membership) {
        account.removeMembership(membership);
        accountRepository.save(account);
    }

    public void addPerkToProfile(Account account, Perk perk) {
        account.addPerkToProfile(perk);
        accountRepository.save(account);
    }

    public void removePerkFromProfile(Account account, Perk perk) {
        account.removePerkFromProfile(perk);
        accountRepository.save(account);
    }
}
