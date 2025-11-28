package com.example.perkmanager.repositories;

import com.example.perkmanager.model.Perk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Perk entity operations.
 * Provides CRUD operations for perk management.
 * Extends JpaRepository to inherit standard database operations.
 *
 */
@Repository
public interface PerkRepository extends JpaRepository<Perk, Long> {
}
