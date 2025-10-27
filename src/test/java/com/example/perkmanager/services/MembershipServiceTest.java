package com.example.perkmanager.services;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.repositories.MembershipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MembershipServiceTest {

    private MembershipRepository membershipRepository;
    private MembershipService membershipService;

    @BeforeEach
    void setUp() {
        membershipRepository = mock(MembershipRepository.class);
        membershipService = new MembershipService(membershipRepository);
    }

    @Test
    void getAllMemberships_shouldReturnList() {
        Membership m1 = new Membership("Card", "Visa", "Visa Rewards");
        Membership m2 = new Membership("AirMiles", "CAA", "CAA Rewards");
        when(membershipRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<Membership> memberships = membershipService.getAllMemberships();

        assertEquals(2, memberships.size());
        assertTrue(memberships.contains(m1));
        assertTrue(memberships.contains(m2));
    }

    @Test
    void findById_existingId_shouldReturnMembership() {
        Membership membership = new Membership("Card", "Visa", "Visa Rewards");
        when(membershipRepository.findById(1L)).thenReturn(Optional.of(membership));

        Optional<Membership> result = membershipService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Visa Rewards", result.get().getDescription());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(membershipRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Membership> result = membershipService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void createMembership_shouldSaveMembership() {
        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArguments()[0]);

        Membership membership = membershipService.createMembership("Card", "Visa", "Visa Rewards");

        verify(membershipRepository).save(captor.capture());
        assertEquals("Visa", captor.getValue().getOrganizationName());
        assertEquals("Card", membership.getType());
    }
}
