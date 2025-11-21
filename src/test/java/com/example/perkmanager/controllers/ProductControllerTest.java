package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

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

    @Test
    void addProductTooLong() {
        String longName = "a".repeat(101); // assumes max 100
        String longCompany = "b".repeat(101);
        String longDesc = "c".repeat(201); // assumes max 200

        String view = productController.addProduct(longName, longCompany, longDesc, model);

        assertEquals("add-product", view);
        verify(model).addAttribute(eq("fieldErrors"), any(Map.class));
        verifyNoInteractions(productService);
    }

    @Test
    void addProductTrimsInput() {
        String name = "  My Product  ";
        String company = "  My Company  ";
        String description = "  My Description  ";

        productController.addProduct(name, company, description, model);

        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> companyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);

        verify(productService).createProduct(
                nameCaptor.capture(),
                companyCaptor.capture(),
                descCaptor.capture()
        );

        assertEquals("My Product", nameCaptor.getValue());
        assertEquals("My Company", companyCaptor.getValue());
        assertEquals("My Description", descCaptor.getValue());
    }
}
