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

        // Trim inputs
        String nameTrim = (name != null) ? name.trim() : null;
        String companyTrim = (company != null) ? company.trim() : null;
        String descTrim = (description != null) ? description.trim() : null;

        Map<String, String> fieldErrors = new HashMap<>();
        if (nameTrim == null || nameTrim.isEmpty()) {
            fieldErrors.put("name", "Name is required");
        } else if (nameTrim.length() > 100) {
            fieldErrors.put("name", "Name must be at most 100 characters");
        }

        if (companyTrim == null || companyTrim.isEmpty()) {
            fieldErrors.put("company", "Company is required");
        } else if (companyTrim.length() > 100) {
            fieldErrors.put("company", "Company must be at most 100 characters");
        }

        if (descTrim == null || descTrim.isEmpty()) {
            fieldErrors.put("description", "Description is required");
        } else if (descTrim.length() > 500) {
            fieldErrors.put("description", "Description must be at most 500 characters");
        }

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("error", "Please fix the errors below");
            model.addAttribute("product", new Product());
            return "add-product";
        }

        productService.createProduct(nameTrim, companyTrim, descTrim);
        return "redirect:/products";
    }
}
