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

/**
 * Service class for managing account-related business logic.
 * Handles account creation, authentication, and linking accounts to perks and memberships.
 * All operations are transactional to ensure data consistency.
 *
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an AccountService with the specified repository and password encoder.
     *
     * @param accountRepository the repository for account data access
     * @param passwordEncoder   the encoder for password hashing
     */
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new account with the specified username and password.
     * The password is encoded before storage for security.
     *
     * @param username the desired username (must be unique)
     * @param password the plain text password (will be encoded)
     * @return the newly created account
     * @throws IllegalArgumentException if the username already exists
     */
    public Account createAccount(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    /**
     * Finds an account by its username.
     *
     * @param username the username to search for
     * @return an Optional containing the account if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    /**
     * Links a perk to its creator account.
     * Establishes the bidirectional relationship between the account and perk.
     *
     * @param account the account that created the perk
     * @param perk    the perk to link to the account
     */
    public void linkPerkToCreator(Account account, Perk perk) {
        account.addPerk(perk);
        perk.setCreator(account);
        accountRepository.save(account);
    }

    /**
     * Adds a membership to an account.
     * Allows users to associate themselves with memberships (e.g., Air Miles, CAA, Visa).
     * When a logged-in user searches perks, they can filter perks by their memberships.
     *
     * @param account    the account to add the membership to
     * @param membership the membership to add
     * @throws IllegalArgumentException if the membership is already associated with the account
     */
    public void addMembership(Account account, Membership membership) {
        // When a logged-in user searches perks, you can filter: perk.getMembership() in account.getMemberships()
        account.addMembership(membership);
        accountRepository.save(account);
    }

    /**
     * Removes a membership from an account.
     * <p>
     * Used when a user edits their profile to unlink a previously added membership.
     *
     * @param account    the account whose membership should be removed
     * @param membership the membership to detach
     */
    public void removeMembership(Account account, Membership membership) {
        account.removeMembership(membership);
        accountRepository.save(account);
    }

    /**
     * Adds a perk to the user's saved perks list (their profile).
     * <p>
     * This does not affect ownership—only association with the user's profile.
     *
     * @param account the account saving the perk
     * @param perk    the perk to save
     */
    public void addPerkToProfile(Account account, Perk perk) {
        account.addPerkToProfile(perk);
        accountRepository.save(account);
    }

    /**
     * Removes a perk from the user's saved perks list (their profile).
     *
     * @param account the account removing the perk
     * @param perk    the perk to un-save from the profile
     */
    public void removePerkFromProfile(Account account, Perk perk) {
        account.removePerkFromProfile(perk);
        accountRepository.save(account);
    }
}
