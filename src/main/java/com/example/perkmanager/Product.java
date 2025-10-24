package com.example.perkmanager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Represents a product or service for which the perk applies.
 */
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    //The name of the product. Ex: KitKat
    @Column(name = "name")
    private String name;
    //Optional: Description of the product
    @Column(name = "description", nullable = true)
    private String description;
    //The company the product is associated with. Ex: Nestle
    @Column(name = "company")
    private String company;

    public Product() {}

    public Product(String name, String company) {
        this.name = name;
        this.company = company;
    }

    public Product(String name, String description, String company) {
        this.name = name;
        this.description = description;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
