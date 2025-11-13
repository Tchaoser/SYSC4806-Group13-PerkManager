package com.example.perkmanager.repositories;

import com.example.perkmanager.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Membership entity operations.
 * Provides CRUD operations for membership management.
 * Extends JpaRepository to inherit standard database operations.
 *
 */
@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
