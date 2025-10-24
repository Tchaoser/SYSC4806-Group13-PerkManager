package com.example.perkmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.*;

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
    void getId() {
        perk.setId(1l);
        assertEquals(1l, perk.getId());
    }

    @Test
    void setId() {
        perk.setId(2l);
        assertEquals(2l, perk.getId());
    }

    @Test
    void getBenefit(){
        assertEquals("Free Wifi On West Jet Flight", perk.getBenefit());
    }

    @Test
    void setBenefit(){
        perk.setBenefit("Free Wifi On Air Canada Flight");
        assertEquals("Free Wifi On Air Canada Flight", perk.getBenefit());
    }

    @Test
    void getDate(){
        assertEquals(2030, perk.getExpiryDate().get(Calendar.YEAR));
        assertEquals(Calendar.NOVEMBER, perk.getExpiryDate().get(Calendar.MONTH));
        assertEquals(5, perk.getExpiryDate().get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void setDate(){
        perk.setExpiryDate(new GregorianCalendar(2027, Calendar.JUNE, 24));
        assertEquals(2027, perk.getExpiryDate().get(Calendar.YEAR));
        assertEquals(Calendar.JUNE, perk.getExpiryDate().get(Calendar.MONTH));
        assertEquals(24, perk.getExpiryDate().get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void getRegion() {
        assertEquals("Europe", perk.getRegion());
    }

    @Test
    void setRegion() {
        perk.setRegion("North America");
        assertEquals("North America", perk.getRegion());
    }


    @Test
    void getMembership() {
        assertEquals(membership1, perk.getMembership());
    }

    @Test
    void setMembership() {
        Membership membership2 = new Membership("Airline Loyalty Program","Air Canada", "Air Canada Loyalty Program");
        perk.setMembership(membership2);
        assertEquals(membership2, perk.getMembership());
    }

    @Test
    void getProduct() {
        assertEquals(product1, perk.getProduct());
    }

    @Test
    void setProduct() {
        Product product2 = new Product("Flight", "Flight with Air Canada", "Air Canada");
        perk.setProduct(product2);
        assertEquals(product2, perk.getProduct());
    }

    @Test
    void getUpvotes() {
        Account account = new Account();
        assertEquals(0, perk.getUpvotes());
        perk.addUpvote(account);
        assertEquals(1, perk.getUpvotes());
        perk.removeUpvote(account);
        assertEquals(0, perk.getUpvotes());
    }

    @Test
    void setUpvotesList() {
        assertEquals(0, perk.getUpvotes());
        Set<Account> upvotes = new HashSet<>();
        for (int i = 0; i < 10; i++){
            upvotes.add(new Account());
        }
        perk.setUpvoteList(upvotes);
        assertEquals(10, perk.getUpvotes());
        assertEquals(upvotes, perk.getUpvoteList());
    }

    //Identical to setUpvotesList()
    @Test
    void getUpvotesList() {
        assertEquals(0, perk.getUpvotes());
        Set<Account> upvotes = perk.getUpvoteList();
        assertEquals(0, upvotes.size());

        upvotes = new HashSet<>();
        for (int i = 0; i < 50; i++){
            upvotes.add(new Account());
        }
        perk.setUpvoteList(upvotes);
        assertEquals(50, perk.getUpvoteList().size());
        assertEquals(upvotes, perk.getUpvoteList());
    }

    @Test
    void getDownvotes() {
        Account account = new Account();
        assertEquals(0, perk.getDownvotes());
        perk.addDownvote(account);
        assertEquals(1, perk.getDownvotes());
        perk.removeDownvote(account);
        assertEquals(0, perk.getDownvotes());
    }

    @Test
    void getDownvotesList() {
        assertEquals(0, perk.getDownvotes());
        Set<Account> downvotes = perk.getDownvoteList();
        assertEquals(0, downvotes.size());
        downvotes = new HashSet<>();
        for (int i = 0; i < 10; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);
        assertEquals(10, perk.getDownvotes());
        assertEquals(downvotes, perk.getDownvoteList());
    }

    //Identical to getDownvotesList()
    @Test
    void setDownvotesList() {
        assertEquals(0, perk.getDownvotes());
        Set<Account> downvotes = perk.getDownvoteList();
        assertEquals(0, downvotes.size());
        downvotes = new HashSet<>();
        for (int i = 0; i < 50; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);
        assertEquals(50, perk.getDownvotes());
        assertEquals(downvotes, perk.getDownvoteList());
    }

    @Test
    void addUpvote() {
        assertEquals(0, perk.getUpvotes());
        Account upvote = new Account();
        perk.addUpvote(upvote);
        assertEquals(1, perk.getUpvotes());
        assertTrue(perk.getUpvoteList().contains(upvote));
    }

    @Test
    void addDownvote() {
        assertEquals(0, perk.getDownvotes());
        Account downvote = new Account();
        perk.addDownvote(downvote);
        assertEquals(1, perk.getDownvotes());
        assertTrue(perk.getDownvoteList().contains(downvote));
    }

    @Test
    void removeUpvote() {
        assertEquals(0, perk.getUpvotes());
        Account upvote = new Account();
        perk.addUpvote(upvote);
        assertEquals(1, perk.getUpvotes());
        assertTrue(perk.removeUpvote(upvote));
        assertEquals(0, perk.getUpvotes());
        assertFalse(perk.removeUpvote(upvote));
        assertEquals(0, perk.getUpvotes());
    }

    @Test
    void removeDownvote() {
        assertEquals(0, perk.getDownvotes());
        Account downvote = new Account();
        perk.addDownvote(downvote);
        assertEquals(1, perk.getDownvotes());
        assertTrue(perk.removeDownvote(downvote));
        assertEquals(0, perk.getDownvotes());
        assertFalse(perk.removeDownvote(downvote));
        assertEquals(0, perk.getDownvotes());
    }

    @Test
    void getRating() {
        assertEquals(0, perk.getDownvotes());
        Set<Account> downvotes = perk.getDownvoteList();
        assertEquals(0, downvotes.size());
        downvotes = new HashSet<>();
        for (int i = 0; i < 10; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);

        assertEquals(0, perk.getUpvotes());
        Set<Account> upvotes = perk.getUpvoteList();
        assertEquals(0, upvotes.size());

        upvotes = new HashSet<>();
        for (int i = 0; i < 50; i++){
            upvotes.add(new Account());
        }
        perk.setUpvoteList(upvotes);

        assertEquals(40, perk.getRating());

        for (int i = 0; i < 100; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);

        assertEquals(-60, perk.getRating());
    }

    @Test
    void getTotalRatings() {
        assertEquals(0, perk.getDownvotes());
        Set<Account> downvotes = perk.getDownvoteList();
        assertEquals(0, downvotes.size());
        downvotes = new HashSet<>();
        for (int i = 0; i < 10; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);

        assertEquals(0, perk.getUpvotes());
        Set<Account> upvotes = perk.getUpvoteList();
        assertEquals(0, upvotes.size());

        upvotes = new HashSet<>();
        for (int i = 0; i < 50; i++){
            upvotes.add(new Account());
        }
        perk.setUpvoteList(upvotes);

        assertEquals(60, perk.getTotalRatings());

        for (int i = 0; i < 100; i++){
            downvotes.add(new Account());
        }
        perk.setDownvoteList(downvotes);

        assertEquals(160, perk.getTotalRatings());
    }
}