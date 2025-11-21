package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // List all products
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    // Show the "add product" form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    // Handle form submit to create a product
    @PostMapping("/add")
    public String addProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String description,
            Model model) {

        Map<String, String> fieldErrors = new HashMap<>();
        if (name == null || name.trim().isEmpty()) {
            fieldErrors.put("name", "Name is required");
        }
        if (company == null || company.trim().isEmpty()) {
            fieldErrors.put("company", "Company is required");
        }
        if (description == null || description.trim().isEmpty()) {
            fieldErrors.put("description", "Description is required");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "Please complete all required fields");
            model.addAttribute("product", new Product());
            return "add-product";
        }

        productService.createProduct(name.trim(), company.trim(), description.trim());
        return "redirect:/products";
    }
}
