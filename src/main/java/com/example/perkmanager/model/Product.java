package com.example.perkmanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false) //The name of the product. Ex: KitKat
    private String name;

    @Column(name = "description", nullable = true) //Optional: Description of the product
    private String description;

    @Column(name = "company", nullable = false) //The company the product is associated with. Ex: Nestle
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
}
