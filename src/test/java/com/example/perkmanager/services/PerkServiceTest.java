package com.example.perkmanager.services;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Product;
import com.example.perkmanager.repositories.PerkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerkServiceTest {

    private PerkRepository perkRepository;
    private PerkService perkService;

    @BeforeEach
    void setUp() {
        perkRepository = mock(PerkRepository.class);
        perkService = new PerkService(perkRepository);
    }

    @Test
    void createPerk_shouldSavePerkAndLinkToCreator() {
        Account account = new Account();
        Membership membership = new Membership("Card", "Visa", "Visa Rewards");
        Product product = new Product("Movie", "Cinema");

        ArgumentCaptor<Perk> captor = ArgumentCaptor.forClass(Perk.class);
        when(perkRepository.save(any(Perk.class))).thenAnswer(i -> i.getArguments()[0]);

        Perk perk = perkService.createPerk(account, membership, product, "10% off", null, "Toronto");

        verify(perkRepository).save(captor.capture());
        assertEquals("10% off", captor.getValue().getBenefit());
        assertTrue(account.getPerks().contains(perk));
        assertEquals("Toronto", perk.getRegion());
    }

    @Test
    void getAllPerks_shouldReturnAllPerks() {
        List<Perk> perks = Arrays.asList(new Perk(), new Perk());
        when(perkRepository.findAll()).thenReturn(perks);

        List<Perk> result = perkService.getAllPerks();

        assertEquals(2, result.size());
        verify(perkRepository).findAll();
    }

    @Test
    void findById_shouldReturnOptionalPerk() {
        Perk perk = new Perk();
        when(perkRepository.findById(1L)).thenReturn(Optional.of(perk));

        Optional<Perk> result = perkService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(perk, result.get());
    }

    @Test
    void upvotePerk_shouldAddUpvoteAndRemoveDownvote() {
        Account user = new Account();
        Perk perk = new Perk();
        perk.getDownvotedBy().add(user);

        when(perkRepository.findById(1L)).thenReturn(Optional.of(perk));

        perkService.upvotePerk(1L, user);

        assertTrue(perk.getUpvotedBy().contains(user));
        assertFalse(perk.getDownvotedBy().contains(user));
        verify(perkRepository).save(perk);
    }

    @Test
    void downvotePerk_shouldAddDownvoteAndRemoveUpvote() {
        Account user = new Account();
        Perk perk = new Perk();
        perk.getUpvotedBy().add(user);

        when(perkRepository.findById(1L)).thenReturn(Optional.of(perk));

        perkService.downvotePerk(1L, user);

        assertTrue(perk.getDownvotedBy().contains(user));
        assertFalse(perk.getUpvotedBy().contains(user));
        verify(perkRepository).save(perk);
    }

    @Test
    void filterPerks_shouldFilterByMembershipAndRegion() {
        Membership m1 = new Membership("Card", "Visa", "Visa Rewards");
        Membership m2 = new Membership("AirMiles", "CAA", "CAA Rewards");
        Perk p1 = new Perk(m1, new Product("Movie", "Cinema"), "10% off");
        p1.setRegion("Toronto");
        Perk p2 = new Perk(m2, new Product("Flight", "WestJet"), "Free flight");
        p2.setRegion("Vancouver");

        when(perkRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Perk> filtered = perkService.filterPerks(
                Optional.of("Card"),
                Optional.of("Toronto"),
                Optional.empty(),
                Optional.empty()
        );

        assertEquals(1, filtered.size());
        assertEquals("10% off", filtered.get(0).getBenefit());
    }

    @Test
    void filterPerks_shouldFilterByExpiryOnlyAndUserMemberships() {
        Membership m1 = new Membership("Card", "Visa", "Visa Rewards");
        Membership m2 = new Membership("AirMiles", "CAA", "CAA Rewards");

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DATE, 10);
        Calendar past = Calendar.getInstance();
        past.add(Calendar.DATE, -10);

        Perk activePerk = new Perk(m1, new Product("Movie", "Cinema"), "10% off");
        activePerk.setExpiryDate(future);

        Perk expiredPerk = new Perk(m2, new Product("Flight", "WestJet"), "Expired");
        expiredPerk.setExpiryDate(past);

        when(perkRepository.findAll()).thenReturn(Arrays.asList(activePerk, expiredPerk));

        Set<Membership> userMemberships = new HashSet<>(Collections.singletonList(m1));
        List<Perk> filtered = perkService.filterPerks(
                Optional.empty(),
                Optional.empty(),
                Optional.of(true),
                Optional.of(userMemberships)
        );

        assertEquals(1, filtered.size());
        assertEquals("10% off", filtered.get(0).getBenefit());
    }


    @Test
    void sortPerks_shouldSortByRatingAscendingAndDescending() {
        Perk low = new Perk();
        Account a1 = new Account(); a1.setUsername("a1"); a1.setPassword("p");
        Account a2 = new Account(); a2.setUsername("a2"); a2.setPassword("p");
        Account a3 = new Account(); a3.setUsername("a3"); a3.setPassword("p");
        Account a4 = new Account(); a4.setUsername("a4"); a4.setPassword("p");
        Account a5 = new Account(); a5.setUsername("a5"); a5.setPassword("p");

        low.getUpvotedBy().add(a1);
        low.getDownvotedBy().add(a2); // rating = 0

        Perk high = new Perk();
        high.getUpvotedBy().add(a3);
        high.getUpvotedBy().add(a4); // rating = 2

        Perk mid = new Perk();
        mid.getUpvotedBy().add(a5); // rating = 1

        List<Perk> perks = Arrays.asList(low, high, mid);

        // Ascending
        List<Perk> ascSorted = perkService.sortPerks(perks, Optional.of("rating"), Optional.of("asc"));
        assertEquals(Arrays.asList(low, mid, high), ascSorted);

        // Descending
        List<Perk> descSorted = perkService.sortPerks(perks, Optional.of("rating"), Optional.of("desc"));
        assertEquals(Arrays.asList(high, mid, low), descSorted);
    }

    @Test
    void sortPerks_shouldSortByExpiryAscendingAndDescending() {
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.DATE, 1);
        Calendar later = Calendar.getInstance();
        later.add(Calendar.DATE, 10);

        Perk p1 = new Perk();
        p1.setExpiryDate(later);
        Perk p2 = new Perk();
        p2.setExpiryDate(soon);

        List<Perk> perks = Arrays.asList(p1, p2);

        // Ascending (soonest first)
        List<Perk> ascSorted = perkService.sortPerks(perks, Optional.of("expiry"), Optional.of("asc"));
        assertEquals(p2, ascSorted.get(0));

        // Descending (latest first)
        List<Perk> descSorted = perkService.sortPerks(perks, Optional.of("expiry"), Optional.of("desc"));
        assertEquals(p1, descSorted.get(0));
    }
}
