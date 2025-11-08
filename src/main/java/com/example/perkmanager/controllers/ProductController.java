package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam String name,
            @RequestParam String company,
            @RequestParam String description
    ) {
        productService.createProduct(name, company, description);
        return "redirect:/products";
    }
}
