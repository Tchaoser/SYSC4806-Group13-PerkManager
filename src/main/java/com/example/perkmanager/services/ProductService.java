package com.example.perkmanager.services;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing product-related business logic.
 * Handles CRUD operations for products that can be associated with perks.
 * All operations are transactional to ensure data consistency.
 *
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

  /**
   * Constructs a ProductService with the specified repository.
   *
   * @param productRepository the repository for product data access
   */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

  /**
   * Retrieves all products in the system.
   *
   * @return a list of all products
   */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

  /**
   * Finds a product by its unique identifier.
   *
   * @param id the product ID
   * @return an Optional containing the product if found, empty otherwise
   */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

  /**
   * Creates a new product with the specified details.
   *
   * @param name the name of the product (e.g., KitKat, Flight Ticket)
   * @param company the company associated with the product (e.g., Nestle, WestJet)
   * @param description optional description of the product
   * @return the newly created product
   */
    public Product createProduct(String name, String company, String description) {
        Product product = new Product(name, description, company);
        return productRepository.save(product);
    }
}
