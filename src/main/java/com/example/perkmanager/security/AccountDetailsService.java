package com.example.perkmanager.security;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.services.AccountService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Spring Security's UserDetailsService.
 * Loads user account information from the database for authentication purposes.
 * Converts Account entities to Spring Security UserDetails objects.
 *
 */
@Service
public class AccountDetailsService implements UserDetailsService {

    private final AccountService accountService;

  /**
   * Constructs an AccountUserDetailsService with the specified AccountService.
   *
   * @param accountService the service for account operations
   */
    public AccountDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

  /**
   * Loads user details by username for Spring Security authentication.
   * Retrieves the account from the database and converts it to a UserDetails object.
   *
   * @param username the username to load
   * @return a UserDetails object containing the user's authentication information
   * @throws UsernameNotFoundException if the user with the given username is not found
   */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles("USER")
                .build();
    }
}
