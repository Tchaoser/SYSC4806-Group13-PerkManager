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
        account = new Account("Bob", "123456");
        account.setId(1L);
        assertNotNull(account);
    }

    @Test
    void getSetId() {
        account.setId(1L);
        assertEquals(1L, account.getId());
        account.setId(2L);
        assertEquals(2L, account.getId());
    }

    @Test
    void getUsername() {
        assertEquals("Bob", account.getUsername());
    }

    @Test
    void setUsername() {
        account.setUsername("Jim24");
        assertEquals("Jim24", account.getUsername());
    }

    @Test
    void setPassword() {
        account.setPassword("123457");
        assertTrue(account.isCorrectPassword("123457"));
    }

    @Test
    void isCorrectPassword() {
        assertTrue(account.isCorrectPassword("123456"));
    }

    @Test
    void setPerks() {
        Set<Perk> perks = new HashSet<>();
        Perk p = new Perk();
        perks.add(p);
        account.setPerks(perks);
        assertEquals(perks, account.getPerks());
    }

    @Test
    void addPerk() {
        assertEquals(0, account.getPerks().size());
        Perk perk = new Perk();
        account.addPerk(perk);
        assertTrue(account.getPerks().contains(perk));
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
