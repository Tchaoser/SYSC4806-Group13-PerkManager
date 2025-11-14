package com.example.perkmanager.model;

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
    void getSetId() {
        product1.setId(1l);
        assertEquals(product1.getId(), 1l);
        product1.setId(2l);
        assertEquals(product1.getId(), 2l);
    }

    @Test
    void getSetName() {
        assertEquals("Tylenol", product1.getName());
        product1.setName("Advil");
        assertEquals("Advil", product1.getName());
        assertEquals("Medicine", product1.getDescription());
        assertEquals("Kenvue", product1.getCompany());
    }

    @Test
    void getSetDescription() {
        assertEquals("Medicine", product1.getDescription());
        product1.setDescription("Pain Relief");
        assertEquals("Tylenol", product1.getName());
        assertEquals("Pain Relief", product1.getDescription());
        assertEquals("Kenvue", product1.getCompany());
    }

    @Test
    void getSetCompany() {
        assertEquals("Kenvue", product1.getCompany());
        product1.setCompany("Pfizer");
        assertEquals("Tylenol", product1.getName());
        assertEquals("Medicine", product1.getDescription());
        assertEquals("Pfizer", product1.getCompany());
    }
}