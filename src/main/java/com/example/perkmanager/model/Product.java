package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a product within the Perk Manager system.
 * <p>
 * Products are associated with perks and typically belong to a company.
 * Examples include flight services, food items, retail goods, or hotel brands.
 */
@Entity
@Table(name = "products")
public class Product {

  /**
   * Unique identifier for this product.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * The name of the product.
   * <p>
   * Example: "KitKat", "Flight Booking", "Hotel Stay"
   */
  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * A human-readable description of the product.
   * <p>
   * Example: "Chocolate bar with wafer layers"
   */
  @NotBlank
  @Column(name = "description", nullable = false)
  private String description;

  /**
   * The company or brand that owns or provides this product.
   * <p>
   * Example: "Nestlé", "Air Canada"
   */
  @NotBlank
  @Column(name = "company", nullable = false)
  private String company;

  /**
   * Default constructor for JPA.
   */
  public Product() {}

  /**
   * Creates a product with the specified name and company.
   *
   * @param name the name of the product
   * @param company the company providing the product
   */
  public Product(String name, String company) {
    this.name = name;
    this.company = company;
  }

  /**
   * Creates a product with the specified name, description, and company.
   *
   * @param name the product name
   * @param description a description of the product
   * @param company the company providing the product
   */
  public Product(String name, String description, String company) {
    this.name = name;
    this.description = description;
    this.company = company;
  }

  /**
   * Returns the unique product ID.
   *
   * @return the product ID
   */
  public Long getId() { return id; }

  /**
   * Sets the unique product ID.
   *
   * @param id the new product ID
   */
  public void setId(Long id) { this.id = id; }

  /**
   * Returns the product name.
   *
   * @return the name of the product
   */
  public String getName() { return name; }

  /**
   * Updates the product name.
   *
   * @param name the new product name
   */
  public void setName(String name) { this.name = name; }

  /**
   * Returns the product description.
   *
   * @return the product description
   */
  public String getDescription() { return description; }

  /**
   * Updates the product description.
   *
   * @param description the new product description
   */
  public void setDescription(String description) { this.description = description; }

  /**
   * Returns the company associated with the product.
   *
   * @return the company name
   */
  public String getCompany() { return company; }

  /**
   * Updates the company associated with the product.
   *
   * @param company the new company name
   */
  public void setCompany(String company) { this.company = company; }
}
