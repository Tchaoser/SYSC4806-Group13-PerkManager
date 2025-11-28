package com.example.perkmanager.repositories;

import com.example.perkmanager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity operations.
 * Provides CRUD operations for product management.
 * Extends JpaRepository to inherit standard database operations.
 *
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
