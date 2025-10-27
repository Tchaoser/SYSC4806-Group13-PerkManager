package com.example.perkmanager.services;

import com.example.perkmanager.model.Account;
import com.example.perkmanager.model.Membership;
import com.example.perkmanager.model.Perk;
import com.example.perkmanager.model.Product;
import com.example.perkmanager.repositories.PerkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PerkService {

    private final PerkRepository perkRepository;

    public PerkService(PerkRepository perkRepository) {
        this.perkRepository = perkRepository;
    }

    // Fetch all perks
    @Transactional(readOnly = true)
    public List<Perk> getAllPerks() {
        return perkRepository.findAll();
    }

    // Fetch by ID
    @Transactional(readOnly = true)
    public Optional<Perk> findById(Long id) {
        return perkRepository.findById(id);
    }

    // Create a new perk
    public Perk createPerk(Account creator, Membership membership, Product product, String benefit, Calendar expiryDate, String region) {
        Perk perk = new Perk();
        perk.setCreator(creator);
        perk.setMembership(membership);
        perk.setProduct(product);
        perk.setBenefit(benefit);
        perk.setExpiryDate(expiryDate);
        perk.setRegion(region);

        // Link perk to creator before saving
        creator.addPerk(perk);

        return perkRepository.save(perk);
    }

    //TODO: Consider moving all filtering and sorting logic to repository-level for scalability
    /**
     * Filter perks optionally by:
     * - membershipType: string (or null)
     * - region: string (or null)
     * - activeOnly: boolean (or null)
     * - userMemberships: Set<Membership> (for logged-in users)
     */
    @Transactional(readOnly = true)
    public List<Perk> filterPerks(Optional<String> membershipType, Optional<String> region, Optional<Boolean> activeOnly, Optional<Set<Membership>> userMemberships) {
        List<Perk> perks = perkRepository.findAll();

        if (membershipType.isPresent()) {
            String typeFilter = membershipType.get().toLowerCase();
            perks = perks.stream()
                    .filter(p -> p.getMembership() != null && typeFilter.equals(p.getMembership().getType().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (region.isPresent()) {
            String regionFilter = region.get().toLowerCase();
            perks = perks.stream()
                    .filter(p -> p.getRegion() != null && p.getRegion().toLowerCase().contains(regionFilter))
                    .collect(Collectors.toList());
        }

        if (activeOnly.isPresent() && activeOnly.get()) {
            Calendar now = Calendar.getInstance();
            perks = perks.stream()
                    .filter(p -> p.getExpiryDate() == null || p.getExpiryDate().after(now))
                    .collect(Collectors.toList());
        }

        if (userMemberships.isPresent() && !userMemberships.get().isEmpty()) {
            Set<Membership> memberships = userMemberships.get();
            perks = perks.stream()
                    .filter(p -> p.getMembership() != null && memberships.contains(p.getMembership()))
                    .collect(Collectors.toList());
        }

        return perks;
    }

    // Upvote a perk
    public void upvotePerk(Long perkId, Account account) {
        Perk perk = perkRepository.findById(perkId)
                .orElseThrow(() -> new NoSuchElementException("Perk not found"));

        // Remove from downvotes if exists
        perk.getDownvotedBy().remove(account);

        // Add to upvotes
        perk.getUpvotedBy().add(account);

        perkRepository.save(perk);
    }

    // Downvote a perk
    public void downvotePerk(Long perkId, Account account) {
        Perk perk = perkRepository.findById(perkId)
                .orElseThrow(() -> new NoSuchElementException("Perk not found"));

        // Remove from upvotes if exists
        perk.getUpvotedBy().remove(account);

        // Add to downvotes
        perk.getDownvotedBy().add(account);

        perkRepository.save(perk);
    }

    // Get perks sorted by rating (upvotes - downvotes)
    @Transactional(readOnly = true)
    public List<Perk> getPerksSortedByRating() {
        return perkRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Perk::getRating).reversed())
                .collect(Collectors.toList());
    }

    // Get perks sorted by expiry date
    @Transactional(readOnly = true)
    public List<Perk> getPerksSortedByExpiry() {
        return perkRepository.findAll().stream()
                .sorted(Comparator.comparing(p -> p.getExpiryDate() != null ? p.getExpiryDate().getTime() : new Date(Long.MAX_VALUE)))
                .collect(Collectors.toList());
    }
}
