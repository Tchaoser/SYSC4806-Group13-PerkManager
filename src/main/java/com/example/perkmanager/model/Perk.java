package com.example.perkmanager.model;

import jakarta.persistence.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a perk in the Perk Manager system.
 * A perk is a discount or benefit tied to a specific membership and product.
 * Perks can have optional expiry dates and regions, and can be upvoted or downvoted by users.
 *
 * @author PerkManager Team
 * @version 1.0
 */
@Entity
@Table(name = "perks")
public class Perk {

    /**
     * The unique identifier for the perk.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The benefit offered by the perk (e.g., "10% off next flight").
     */
    @Column(name = "benefit", nullable = false)
    private String benefit;

    /**
     * The date the perk expires on. Optional - null if the perk does not expire.
     */
    @Column(name = "expiry_date", nullable = true)
    private Calendar expiryDate;

    /**
     * The region where the perk applies. Optional - null if the perk applies everywhere.
     * Multiple regions should be listed in the same string.
     */
    @Column(name = "region", nullable = true)
    private String region;

    /**
     * The membership required to use this perk.
     */
    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    /**
     * The product or service this perk applies to.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The account that created this perk.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Account creator;

    /**
     * The set of accounts that have upvoted this perk.
     */
    @ManyToMany
    @JoinTable(
            name = "perk_upvotes",
            joinColumns = @JoinColumn(name = "perk_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> upvotedBy = new HashSet<>();

    /**
     * The set of accounts that have downvoted this perk.
     */
    @ManyToMany
    @JoinTable(
            name = "perk_downvotes",
            joinColumns = @JoinColumn(name = "perk_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> downvotedBy = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    public Perk() {}

    /**
     * Constructs a new Perk with the specified membership, product, and benefit.
     *
     * @param membership the membership required for this perk
     * @param product the product this perk applies to
     * @param benefit the benefit description
     */
    public Perk(Membership membership, Product product, String benefit) {
        this.membership = membership;
        this.product = product;
        this.benefit = benefit;
    }

    /**
     * Gets the unique identifier of this perk.
     *
     * @return the perk ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier of this perk.
     *
     * @param id the perk ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the benefit description of this perk.
     *
     * @return the benefit description
     */
    public String getBenefit() { return benefit; }

    /**
     * Sets the benefit description of this perk.
     *
     * @param benefit the benefit description to set
     */
    public void setBenefit(String benefit) { this.benefit = benefit; }

    /**
     * Gets the expiry date of this perk.
     *
     * @return the expiry date, or null if the perk does not expire
     */
    public Calendar getExpiryDate() { return expiryDate; }

    /**
     * Sets the expiry date of this perk.
     *
     * @param expiryDate the expiry date to set, or null if the perk does not expire
     */
    public void setExpiryDate(Calendar expiryDate) { this.expiryDate = expiryDate; }

    /**
     * Gets the region where this perk applies.
     *
     * @return the region, or null if the perk applies everywhere
     */
    public String getRegion() { return region; }

    /**
     * Sets the region where this perk applies.
     *
     * @param region the region to set, or null if the perk applies everywhere
     */
    public void setRegion(String region) { this.region = region; }

    /**
     * Gets the membership required for this perk.
     *
     * @return the membership
     */
    public Membership getMembership() { return membership; }

    /**
     * Sets the membership required for this perk.
     *
     * @param membership the membership to set
     */
    public void setMembership(Membership membership) { this.membership = membership; }

    /**
     * Gets the product this perk applies to.
     *
     * @return the product
     */
    public Product getProduct() { return product; }

    /**
     * Sets the product this perk applies to.
     *
     * @param product the product to set
     */
    public void setProduct(Product product) { this.product = product; }

    /**
     * Gets the account that created this perk.
     *
     * @return the creator account
     */
    public Account getCreator() { return creator; }

    /**
     * Sets the account that created this perk.
     *
     * @param creator the creator account to set
     */
    public void setCreator(Account creator) { this.creator = creator; }

    /**
     * Gets the set of accounts that have upvoted this perk.
     *
     * @return the set of accounts that upvoted
     */
    public Set<Account> getUpvotedBy() { return upvotedBy; }

    /**
     * Sets the set of accounts that have upvoted this perk.
     *
     * @param upvotedBy the set of accounts to set
     */
    public void setUpvotedBy(Set<Account> upvotedBy) { this.upvotedBy = upvotedBy; }

    /**
     * Gets the set of accounts that have downvoted this perk.
     *
     * @return the set of accounts that downvoted
     */
    public Set<Account> getDownvotedBy() { return downvotedBy; }

    /**
     * Sets the set of accounts that have downvoted this perk.
     *
     * @param downvotedBy the set of accounts to set
     */
    public void setDownvotedBy(Set<Account> downvotedBy) { this.downvotedBy = downvotedBy; }

    /**
     * Calculates the rating of this perk (upvotes minus downvotes).
     *
     * @return the rating (positive for more upvotes, negative for more downvotes)
     */
    public int getRating() { return upvotedBy.size() - downvotedBy.size(); }

    /**
     * Gets the total number of ratings (upvotes plus downvotes) for this perk.
     *
     * @return the total number of ratings
     */
    public int getTotalRatings() { return upvotedBy.size() + downvotedBy.size(); }

    /**
     * Adds an upvote from the specified account.
     *
     * @param account the account that upvoted
     */
    public void addUpvote(Account account) { upvotedBy.add(account); }

    /**
     * Adds a downvote from the specified account.
     *
     * @param account the account that downvoted
     */
    public void addDownvote(Account account) { downvotedBy.add(account); }

    /**
     * Removes an upvote from the specified account.
     *
     * @param account the account to remove the upvote from
     * @return true if the upvote was removed, false otherwise
     */
    public boolean removeUpvote(Account account) { return upvotedBy.remove(account); }

    /**
     * Removes a downvote from the specified account.
     *
     * @param account the account to remove the downvote from
     * @return true if the downvote was removed, false otherwise
     */
    public boolean removeDownvote(Account account) { return downvotedBy.remove(account); }
}
