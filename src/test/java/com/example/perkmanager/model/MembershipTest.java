package com.example.perkmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class MembershipTest {
    Membership membership;
    @BeforeEach
    void setUp() {
        membership = new Membership("Airline Loyalty Program", "West Jet", "West Jet Rewards Member");
    }

    @Test
    void getId() {
        membership.setId(2l);
        assertEquals(2l, membership.getId());
    }

    @Test
    void setId() {
        membership.setId(2l);
        assertEquals(2l, membership.getId());
    }

    @Test
    void getType() {
        assertEquals("Airline Loyalty Program", membership.getType());
    }

    @Test
    void setType() {
        membership.setType("Store Membership");
        assertEquals("Store Membership", membership.getType());
        assertEquals("West Jet", membership.getOrganizationName());
        assertEquals("West Jet Rewards Member", membership.getDescription());

    }

    @Test
    void getOrganizationName() {
        assertEquals("West Jet", membership.getOrganizationName());
    }

    @Test
    void setOrganizationName() {
        membership.setOrganizationName("Air Canada");
        assertEquals("Airline Loyalty Program", membership.getType());
        assertEquals("Air Canada", membership.getOrganizationName());
        assertEquals("West Jet Rewards Member", membership.getDescription());
    }

    @Test
    void getDescription() {
        assertEquals("West Jet Rewards Member", membership.getDescription());
    }

    @Test
    void setDescription() {
        membership.setDescription("Air Canada Rewards Member");
        assertEquals("Airline Loyalty Program", membership.getType());
        assertEquals("West Jet", membership.getOrganizationName());
        assertEquals("Air Canada Rewards Member", membership.getDescription());
    }
}