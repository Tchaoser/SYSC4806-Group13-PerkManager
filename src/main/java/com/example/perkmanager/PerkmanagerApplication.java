package com.example.perkmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Perk Manager Spring Boot application.
 * This class initializes and starts the Spring Boot application context,
 * enabling auto-configuration of Spring components, JPA repositories, and web services.
 *
 * <p>The Perk Manager application allows users to create profiles, associate memberships,
 * and manage perks (discounts or benefits) tied to specific memberships and products.
 *
 * @author Peter
 * @version 1.0
 */
@SpringBootApplication
public class PerkmanagerApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(PerkmanagerApplication.class, args);
    }

}
