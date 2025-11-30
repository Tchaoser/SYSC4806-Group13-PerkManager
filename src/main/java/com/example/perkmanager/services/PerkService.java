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

/**
 * Service class for managing perk-related business logic.
 * Handles CRUD operations, filtering, sorting, and voting functionality for perks.
 * All operations are transactional to ensure data consistency.
 *
 */
@Service
@Transactional
public class PerkService {

    private final PerkRepository perkRepository;

    /**
     * Constructs a PerkService with the specified repository.
     *
     * @param perkRepository the repository for perk data access
     */
    public PerkService(PerkRepository perkRepository) {
        this.perkRepository = perkRepository;
    }

    /**
     * Retrieves all perks in the system.
     *
     * @return a list of all perks
     */
    @Transactional(readOnly = true)
    public List<Perk> getAllPerks() {
        return perkRepository.findAll();
    }

    /**
     * Finds a perk by its unique identifier.
     *
     * @param id the perk ID
     * @return an Optional containing the perk if found, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<Perk> findById(Long id) {
        return perkRepository.findById(id);
    }

    /**
     * Creates a new perk with the specified details.
     * Links the perk to its creator account before saving.
     *
     * @param creator    the account that created the perk
     * @param membership the membership required for this perk
     * @param product    the product this perk applies to
     * @param benefit    the description of the benefit offered
     * @param expiryDate the expiry date of the perk (can be null)
     * @param region     the region where the perk applies (can be null)
     * @return the newly created perk
     */
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

    /**
     * Filters perks based on optional criteria.
     * Supports filtering by membership type, region, expiry status, and user memberships.
     * All filters are optional and can be combined.
     *
     * @param membershipType  optional filter by membership type (case-insensitive)
     * @param region          optional filter by region (case-insensitive substring match)
     * @param expiryOnly      if true, only returns perks with future expiry dates
     * @param userMemberships optional set of user memberships to filter perks that match
     * @return a filtered list of perks matching all specified criteria
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
                .filter(p -> userMemberships.isEmpty() ||
                        (p.getMembership() != null && userMemberships.get().contains(p.getMembership())))
                .collect(Collectors.toList());
    }

    /**
     * Upvotes a perk for the specified account.
     * If the account had previously downvoted the perk, the downvote is removed first.
     *
     * @param perkId  the ID of the perk to upvote
     * @param account the account that is upvoting
     * @throws NoSuchElementException if the perk with the given ID is not found
     */
    public void toggleUpvotePerk(Long perkId, Account account) {
        Perk perk = perkRepository.findById(perkId)
                .orElseThrow(() -> new NoSuchElementException("Perk not found"));

        if (perk.getUpvotedBy().contains(account)) {
            perk.getUpvotedBy().remove(account);
        } else {
            perk.getUpvotedBy().add(account);
            perk.getDownvotedBy().remove(account);
        }

        perkRepository.save(perk);
    }

    /**
     * Downvotes a perk for the specified account.
     * If the account had previously upvoted the perk, the upvote is removed first.
     *
     * @param perkId  the ID of the perk to downvote
     * @param account the account that is downvoting
     * @throws NoSuchElementException if the perk with the given ID is not found
     */
    public void toggleDownvotePerk(Long perkId, Account account) {
        Perk perk = perkRepository.findById(perkId)
                .orElseThrow(() -> new NoSuchElementException("Perk not found"));

        if (perk.getDownvotedBy().contains(account)) {
            perk.getDownvotedBy().remove(account);
        } else {
            perk.getDownvotedBy().add(account);
            perk.getUpvotedBy().remove(account);
        }

        perkRepository.save(perk);
    }

    /**
     * Sorts a list of perks by the specified sort key and direction.
     * Supported sort keys: "rating" (upvotes minus downvotes), "expiry" (expiry date).
     * If an unknown sort key is provided, the list is returned unchanged.
     *
     * @param perks     the list of perks to sort
     * @param sortKey   optional sort key ("rating" or "expiry")
     * @param direction optional sort direction ("asc" or "desc", defaults to "asc")
     * @return a sorted list of perks, or the original list if sort key is empty or unknown
     */
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
