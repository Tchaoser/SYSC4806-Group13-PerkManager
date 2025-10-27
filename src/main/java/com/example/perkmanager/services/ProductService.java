package com.example.perkmanager.services;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Fetch all products
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Fetch product by ID
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    // Create a new product
    public Product createProduct(String name, String company, String description) {
        Product product = new Product(name, description, company);
        return productRepository.save(product);
    }
}
