package com.example.perkmanager.controllers;

import com.example.perkmanager.model.Product;
import com.example.perkmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling product-related operations.
 * Manages the creation and listing of products or services that can be associated with perks.
 *
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructs a ProductController with the specified ProductService.
     *
     * @param productService the service for product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lists all available products in the system.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the products template
     */
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    /**
     * Displays the form for adding a new product.
     * Passes an empty Product object to the view for form binding.
     *
     * @param model the Spring model for passing data to the view
     * @return the name of the add-product template
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    /**
     * Handles the submission of the add product form.
     * Creates a new product with the provided details and redirects to the products list.
     *
     * @param name the name of the product (e.g., KitKat)
     * @param company the company associated with the product (e.g., Nestle)
     * @param description optional description of the product
     * @return redirect to the products list page
     */
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
