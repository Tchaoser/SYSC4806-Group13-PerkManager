package com.example.perkmanager.model;

import jakarta.persistence.*;

/**
 * Represents a product or service in the Perk Manager system.
 * Products can be associated with perks and represent items or services
 * that users can receive benefits for (e.g., flights, movies, consumer goods).
 *
 * @author Peter
 * @version 1.0
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the product (e.g., KitKat, Flight Ticket, Movie Ticket).
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Optional description of the product.
     */
    @Column(name = "description", nullable = true)
    private String description;

    /**
     * The company associated with the product (e.g., Nestle, WestJet, Cineplex).
     */
    @Column(name = "company", nullable = false)
    private String company;

    /**
     * Default constructor for JPA.
     */
    public Product() {}

    /**
     * Constructs a new Product with the specified name and company.
     *
     * @param name the name of the product
     * @param company the company associated with the product
     */
    public Product(String name, String company) {
        this.name = name;
        this.company = company;
    }

    /**
     * Constructs a new Product with the specified name, description, and company.
     *
     * @param name the name of the product
     * @param description the description of the product
     * @param company the company associated with the product
     */
    public Product(String name, String description, String company) {
        this.name = name;
        this.description = description;
        this.company = company;
    }

    /**
     * Gets the unique identifier of this product.
     *
     * @return the product ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier of this product.
     *
     * @param id the product ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the name of this product.
     *
     * @return the product name
     */
    public String getName() { return name; }

    /**
     * Sets the name of this product.
     *
     * @param name the product name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the description of this product.
     *
     * @return the product description, or null if not set
     */
    public String getDescription() { return description; }

    /**
     * Sets the description of this product.
     *
     * @param description the product description to set, or null to clear
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the company associated with this product.
     *
     * @return the company name
     */
    public String getCompany() { return company; }

    /**
     * Sets the company associated with this product.
     *
     * @param company the company name to set
     */
    public void setCompany(String company) { this.company = company; }
}
