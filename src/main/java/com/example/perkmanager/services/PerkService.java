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
     * - expiryOnly: boolean (or null)
     * - userMemberships: Set<Membership> (for logged-in users)
     */
    @Transactional(readOnly = true)
    public List<Perk> filterPerks(Optional<String> membershipType,
                                  Optional<String> region,
                                  Optional<Boolean> expiryOnly,
                                  Optional<Set<Membership>> userMemberships) {

        Calendar now = Calendar.getInstance();

        return perkRepository.findAll().stream()
                // Membership type filter: only if non-empty
                .filter(p -> membershipType.isEmpty() ||
                        membershipType.get().trim().isEmpty() ||
                        (p.getMembership() != null &&
                                membershipType.get().equalsIgnoreCase(p.getMembership().getType())))
                // Region filter: only if non-empty
                .filter(p -> region.isEmpty() ||
                        region.get().trim().isEmpty() ||
                        (p.getRegion() != null &&
                                p.getRegion().toLowerCase().contains(region.get().toLowerCase())))
                // Expiry Only filter (Exclude perks with no expiry)
                .filter(p -> {
                    if (!expiryOnly.orElse(false)) return true; // include all if not filtering
                    if (p.getExpiryDate() == null) return false; // exclude perks with no expiry
                    return p.getExpiryDate().after(now); // only include future expiry dates
                })
                // User memberships filter
                .filter(p -> userMemberships.isEmpty() || userMemberships.get().isEmpty() ||
                        (p.getMembership() != null && userMemberships.get().contains(p.getMembership())))
                .collect(Collectors.toList());
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

    public List<Perk> sortPerks(List<Perk> perks, Optional<String> sortKey, Optional<String> direction) {
        if (sortKey.isEmpty()) return perks;

        boolean asc = !"desc".equalsIgnoreCase(direction.orElse("asc"));
        Comparator<Perk> comparator = null;

        switch (sortKey.get()) {
            case "rating":
                comparator = Comparator.comparingInt(Perk::getRating); //TODO: ensure sorting by rating works correctly when ratings are implemented.
                break;
            case "expiry":
                comparator = Comparator.comparing(
                        Perk::getExpiryDate,
                        Comparator.nullsLast(Comparator.comparingLong(Calendar::getTimeInMillis))
                );
                break;
            default:
                return perks; // unknown sort -> leave as-is
        }

        if (!asc) comparator = comparator.reversed();
        return perks.stream().sorted(comparator).collect(Collectors.toList());
    }

}
