package com.example.perkmanager.repositories;

import com.example.perkmanager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Account entity operations.
 * Provides CRUD operations and custom query methods for account management.
 * Extends JpaRepository to inherit standard database operations.
 *
 * @author PerkManager Team
 * @version 1.0
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Finds an account by its username.
     *
     * @param username the username to search for
     * @return an Optional containing the account if found, empty otherwise
     */
    Optional<Account> findByUsername(String username);
}
