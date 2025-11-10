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
    void getSetId() {
        membership.setId(2l);
        assertEquals(2l, membership.getId());
        membership.setId(3l);
        assertEquals(3l, membership.getId());
    }

    @Test
    void getSetType() {
        assertEquals("Airline Loyalty Program", membership.getType());
        membership.setType("Store Membership");
        assertEquals("Store Membership", membership.getType());
        assertEquals("West Jet", membership.getOrganizationName());
        assertEquals("West Jet Rewards Member", membership.getDescription());
    }

    @Test
    void getSetOrganizationName() {
        assertEquals("West Jet", membership.getOrganizationName());
        membership.setOrganizationName("Air Canada");
        assertEquals("Airline Loyalty Program", membership.getType());
        assertEquals("Air Canada", membership.getOrganizationName());
        assertEquals("West Jet Rewards Member", membership.getDescription());
    }

    @Test
    void getSetDescription() {
        assertEquals("West Jet Rewards Member", membership.getDescription());
        membership.setDescription("Air Canada Rewards Member");
        assertEquals("Airline Loyalty Program", membership.getType());
        assertEquals("West Jet", membership.getOrganizationName());
        assertEquals("Air Canada Rewards Member", membership.getDescription());
    }
}