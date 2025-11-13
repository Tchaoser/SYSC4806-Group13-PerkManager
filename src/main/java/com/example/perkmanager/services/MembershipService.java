package com.example.perkmanager.services;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.repositories.MembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing membership-related business logic.
 * Handles CRUD operations for memberships and provides utility methods for membership management.
 * All operations are transactional to ensure data consistency.
 *
 */
@Service
@Transactional
public class MembershipService {

    private final MembershipRepository membershipRepository;

    /**
     * Constructs a MembershipService with the specified repository.
     *
     * @param membershipRepository the repository for membership data access
     */
    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    /**
     * Retrieves all memberships in the system.
     *
     * @return a list of all memberships
     */
    @Transactional(readOnly = true)
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    /**
     * Retrieves all unique membership types in the system.
     * Returns a sorted list of distinct membership types (e.g., "Credit card", "Air Miles").
     *
     * @return a sorted list of unique membership types
     */
    @Transactional(readOnly = true)
    public List<String> getAllMembershipTypes() {
        return membershipRepository.findAll().stream()
                .map(Membership::getType)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Finds a membership by its unique identifier.
     *
     * @param id the membership ID
     * @return an Optional containing the membership if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Membership> findById(Long id) {
        return membershipRepository.findById(id);
    }

    /**
     * Creates a new membership with the specified details.
     *
     * @param type the type of membership (e.g., Credit card, Air Miles)
     * @param organizationName the organization or company name (e.g., RBC, WestJet, Cineplex)
     * @param description the description of the membership (e.g., West Jet Rewards Member)
     * @return the newly created membership
     */
    public Membership createMembership(String type, String organizationName, String description) {
        Membership membership = new Membership(type, organizationName, description);
        return membershipRepository.save(membership);
    }
}
