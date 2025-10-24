package com.example.perkmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    Product product1;

    @BeforeEach
    void setUp() {
        product1 = new Product("Tylenol", "Medicine", "Kenvue");
    }

    @Test
    void getId() {
        product1.setId(1l);
        assertEquals(product1.getId(), 1l);
    }

    @Test
    void setId() {
        product1.setId(2l);
        assertEquals(product1.getId(), 2l);
    }

    @Test
    void getName() {
        assertEquals("Tylenol", product1.getName());
    }

    @Test
    void setName() {
        product1.setName("Advil");
        assertEquals("Advil", product1.getName());
        assertEquals("Medicine", product1.getDescription());
        assertEquals("Kenvue", product1.getCompany());
    }

    @Test
    void getDescription() {
        assertEquals("Medicine", product1.getDescription());
    }

    @Test
    void setDescription() {
        product1.setDescription("Pain Relief");
        assertEquals("Tylenol", product1.getName());
        assertEquals("Pain Relief", product1.getDescription());
        assertEquals("Kenvue", product1.getCompany());

    }

    @Test
    void getCompany() {
        assertEquals("Kenvue", product1.getCompany());
    }

    @Test
    void setCompany() {
        product1.setCompany("Pfizer");
        assertEquals("Tylenol", product1.getName());
        assertEquals("Medicine", product1.getDescription());
        assertEquals("Pfizer", product1.getCompany());
    }
}