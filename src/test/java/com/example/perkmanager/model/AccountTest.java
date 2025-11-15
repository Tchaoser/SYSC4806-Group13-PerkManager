package com.example.perkmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setUsername("Bob");
        account.setPassword("123456");
        account.setId(1L);
        assertNotNull(account);
    }

    @Test
    void addRemoveMembership() {
        Membership membership = new Membership();

        assertFalse(account.getMemberships().contains(membership));
        account.addMembership(membership);
        assertTrue(account.getMemberships().contains(membership));
        account.removeMembership(membership);
        assertFalse(account.getMemberships().contains(membership));
    }

    @Test
    void getSetId() {
        account.setId(1L);
        assertEquals(1L, account.getId());
        account.setId(2L);
        assertEquals(2L, account.getId());
    }

    @Test
    void getSetUsername() {
        account.setUsername("Bob");
        assertEquals("Bob", account.getUsername());
        account.setUsername("Jim24");
        assertEquals("Jim24", account.getUsername());
    }

    @Test
    void getSetPassword() {
        account.setPassword("123457");
        assertEquals("123457", account.getPassword());
        account.setPassword("1234567");
        assertEquals("1234567", account.getPassword());
    }

    @Test
    void getSetPerks() {
        Set<Perk> perks = new HashSet<>();
        Perk p = new Perk();
        perks.add(p);
        account.setPerks(perks);
        assertEquals(perks, account.getPerks());
        Set<Perk> emptyPerks = new HashSet<>();
        account.setPerks(emptyPerks);
        assertEquals(0, account.getPerks().size());
    }

    @Test
    void addRemovePerk() {
        assertEquals(0, account.getPerks().size());
        Perk perk = new Perk();
        assertFalse(account.getPerks().contains(perk));
        account.addPerk(perk);
        assertTrue(account.getPerks().contains(perk));
        account.removePerk(perk);
        assertFalse(account.getPerks().contains(perk));
    }

    @Test
    void removePerkByObject() {
        Perk perk = new Perk();
        account.addPerk(perk);
        assertTrue(account.removePerk(perk));
        assertFalse(account.getPerks().contains(perk));
    }

    @Test
    void removePerkById() {
        Perk perk = new Perk();
        perk.setId(1L);
        account.addPerk(perk);
        Perk removed = account.removePerkById(1L);
        assertEquals(perk, removed);
        assertEquals(0, account.getPerks().size());
    }
}
