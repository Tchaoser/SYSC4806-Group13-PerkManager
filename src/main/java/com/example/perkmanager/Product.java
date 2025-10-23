package com.example.perkmanager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a product or service for which the perk applies.
 */
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    //The name of the product. Ex:
    private String name;
    //Optional: Description of the product
    private String description;
    //The company the product is associated with. Ex:
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
