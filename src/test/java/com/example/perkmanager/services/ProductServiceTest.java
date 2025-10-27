package com.example.perkmanager.services;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts_shouldReturnList() {
        Product p1 = new Product("Movie", "Cinema");
        Product p2 = new Product("Flight", "Airline");
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertTrue(products.contains(p1));
        assertTrue(products.contains(p2));
    }

    @Test
    void findById_existingId_shouldReturnProduct() {
        Product product = new Product("Movie", "Cinema");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Movie", result.get().getName());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void createProduct_shouldSaveProduct() {
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        Product product = productService.createProduct("Movie", "Cinema", "Blockbuster film");

        verify(productRepository).save(captor.capture());
        assertEquals("Cinema", captor.getValue().getCompany());
        assertEquals("Blockbuster film", product.getDescription());
    }
}
