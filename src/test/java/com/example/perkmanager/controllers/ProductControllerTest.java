package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private ProductController productController;
    private ProductService productService;
    private Model model;

    @BeforeEach
    void setup() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
        model = mock(Model.class);
    }

    @Test
    void listProducts() {
        List<Product> products = List.of(new Product());
        when(productService.getAllProducts()).thenReturn(products);

        String view = productController.listProducts(model);

        assertEquals("products", view);
        verify(model).addAttribute("products", products);
    }

    @Test
    void showAddForm() {
        String view = productController.showAddForm(model);

        assertEquals("add-product", view);
        verify(model).addAttribute(eq("product"), any(Product.class));
    }

    @Test
    void addProduct() {
        String name = "Test Product";
        String company = "Test Company";
        String description = "Test Description";

        String view = productController.addProduct(name, company, description, model);

        assertEquals("redirect:/products", view);
        verify(productService, times(1)).createProduct(name, company, description);
    }
}
