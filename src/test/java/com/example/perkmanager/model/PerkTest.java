package com.example.perkmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PerkTest {
    private Membership membership1;
    private Product product1;
    private Perk perk;

    @BeforeEach
    void setUp() {
        membership1 = new Membership("Airline Loyalty Program", "West Jet", "West Jet Rewards Member");
        product1 = new Product("Flight", "Flight with West Jet", "West Jet");
        perk = new Perk(membership1, product1, "Free Wifi On West Jet Flight");
        perk.setExpiryDate(new GregorianCalendar(2030, Calendar.NOVEMBER, 5));
        perk.setRegion("Europe");
    }

    @Test
    void getSetId() {
        perk.setId(1L);
        assertEquals(1L, perk.getId());
        perk.setId(2L);
        assertEquals(2L, perk.getId());
    }

    @Test
    void getSetBenefit() {
        assertEquals("Free Wifi On West Jet Flight", perk.getBenefit());
        perk.setBenefit("Free Wifi On Air Canada Flight");
        assertEquals("Free Wifi On Air Canada Flight", perk.getBenefit());
    }

    @Test
    void getSetExpiryDate() {
        Calendar newDate = new GregorianCalendar(2027, Calendar.JUNE, 24);
        perk.setExpiryDate(newDate);
        assertEquals(newDate, perk.getExpiryDate());
    }

    @Test
    void getSetRegion() {
        perk.setRegion("North America");
        assertEquals("North America", perk.getRegion());
    }

    @Test
    void getSetMembership() {
        Membership membership2 = new Membership("Airline Loyalty Program","Air Canada", "Air Canada Loyalty Program");
        perk.setMembership(membership2);
        assertEquals(membership2, perk.getMembership());
    }

    @Test
    void getSetProduct() {
        Product product2 = new Product("Flight", "Flight with Air Canada", "Air Canada");
        perk.setProduct(product2);
        assertEquals(product2, perk.getProduct());
    }

    @Test
    void upvoteDownvoteManagement() {
        Account acc1 = new Account();
        Account acc2 = new Account();

        // Initially empty
        assertEquals(0, perk.getUpvotedBy().size());
        assertEquals(0, perk.getDownvotedBy().size());

        // Add upvote
        perk.addUpvote(acc1);
        assertEquals(1, perk.getUpvotedBy().size());
        assertTrue(perk.getUpvotedBy().contains(acc1));

        // Add downvote
        perk.addDownvote(acc2);
        assertEquals(1, perk.getDownvotedBy().size());
        assertTrue(perk.getDownvotedBy().contains(acc2));

        // Remove upvote
        assertTrue(perk.removeUpvote(acc1));
        assertEquals(0, perk.getUpvotedBy().size());

        // Remove downvote
        assertTrue(perk.removeDownvote(acc2));
        assertEquals(0, perk.getDownvotedBy().size());

        // Remove non-existing vote
        assertFalse(perk.removeUpvote(acc1));
        assertFalse(perk.removeDownvote(acc2));
    }

    @Test
    void getRatingAndTotalRatings() {
        Set<Account> upvotes = new HashSet<>();
        Set<Account> downvotes = new HashSet<>();

        for (int i = 0; i < 50; i++) upvotes.add(new Account());
        for (int i = 0; i < 10; i++) downvotes.add(new Account());
        perk.setUpvotedBy(upvotes);
        perk.setDownvotedBy(downvotes);

        assertEquals(40, perk.getRating());
        assertEquals(60, perk.getTotalRatings());

        // Add more downvotes
        for (int i = 0; i < 100; i++) downvotes.add(new Account());
        perk.setDownvotedBy(downvotes);

        assertEquals(-60, perk.getRating());
        assertEquals(160, perk.getTotalRatings());
    }
}
