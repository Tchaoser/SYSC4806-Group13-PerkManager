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
        Account account = new Account("user1", "pass");
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
        Account user = new Account("user", "pass");
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
        Account user = new Account("user", "pass");
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
    void filterPerks_shouldFilterByActiveOnlyAndUserMemberships() {
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
    void getPerksSortedByRating_shouldSortDescending() {
        Perk p1 = new Perk();
        Perk p2 = new Perk();
        Perk p3 = new Perk();

        // simulate rating
        p1.getUpvotedBy().add(new Account("u1", "p"));
        p1.getDownvotedBy().add(new Account("u2", "p")); // rating = 0
        p2.getUpvotedBy().add(new Account("u3", "p")); // rating = 1
        p3.getDownvotedBy().add(new Account("u4", "p")); // rating = -1

        when(perkRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        List<Perk> sorted = perkService.getPerksSortedByRating();

        assertEquals(p2, sorted.get(0)); // highest rating first
    }

    @Test
    void getPerksSortedByExpiry_shouldSortSoonestFirst() {
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.DATE, 1);
        Calendar later = Calendar.getInstance();
        later.add(Calendar.DATE, 5);

        Perk p1 = new Perk();
        p1.setExpiryDate(later);
        Perk p2 = new Perk();
        p2.setExpiryDate(soon);

        when(perkRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Perk> sorted = perkService.getPerksSortedByExpiry();

        assertEquals(p2, sorted.get(0)); // soonest expiry first
    }
}
