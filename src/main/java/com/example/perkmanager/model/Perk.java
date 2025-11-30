package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a perk in the Perk Manager system.
 * A perk is a discount or benefit tied to a specific membership and product.
 * Perks can have optional expiry dates and regions, and can be upvoted or downvoted by users.
 *
 */
@Entity
@Table(name = "perks")
public class Perk {

  public static final int BENEFIT_MAX_LENGTH = 200;
  public static final int REGION_MAX_LENGTH = 100;

  /**
   * Unique identifier for this perk.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * Description of the benefit provided by this perk.
   * <p>
   * Example: "10% off your next flight" or "Free checked bag".
   */
  @NotBlank
  @Column(name = "benefit", nullable = false)
  private String benefit;

  /**
   * Optional expiration date for this perk.
   * Must not be a date in the past.
   */
  @FutureOrPresent(message = "Date must not have already passed")
  @Column(name = "expiry_date", nullable = true)
  private Calendar expiryDate;

  /**
   * Optional region where this perk applies.
   * <p>
   * Example: "Canada", "US + EU", or "Worldwide".
   * When multiple regions apply, they should be listed in the same string.
   */
  @Column(name = "region", nullable = true)
  private String region;

  /**
   * The membership program providing this perk.
   * <p>
   * Example: Air Miles, CAA, Visa, etc.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "membership_id", nullable = false)
  private Membership membership;

  /**
   * The product associated with this perk.
   * <p>
   * Example: Flights, Hotels, Car Rentals, etc.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  /**
   * The account that originally created this perk.
   */
  @NotNull
  @ManyToOne
  @JoinColumn(name = "creator_id", nullable = false)
  private Account creator;

  /**
   * Accounts that have upvoted this perk.
   */
  @ManyToMany
  @JoinTable(
    name = "perk_upvotes",
    joinColumns = @JoinColumn(name = "perk_id"),
    inverseJoinColumns = @JoinColumn(name = "account_id")
  )
  private Set<Account> upvotedBy = new HashSet<>();

  /**
   * Accounts that have downvoted this perk.
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
   * Creates a new perk associated with a membership, product, and benefit description.
   *
   * @param membership the membership providing the perk
   * @param product the product the perk applies to
   * @param benefit the benefit text describing the perk
   */
  public Perk(Membership membership, Product product, String benefit) {
    this.membership = membership;
    this.product = product;
    this.benefit = benefit;
  }

  /**
   * Returns the unique ID of the perk.
   *
   * @return the perk ID
   */
  public Long getId() { return id; }

  /**
   * Sets the unique ID for this perk.
   *
   * @param id the new perk ID
   */
  public void setId(Long id) { this.id = id; }

  /**
   * Returns the description of the perk benefit.
   *
   * @return the benefit text
   */
  public String getBenefit() { return benefit; }

  /**
   * Updates the benefit description of the perk.
   *
   * @param benefit the new benefit text
   */
  public void setBenefit(String benefit) { this.benefit = benefit; }

  /**
   * Returns the expiry date for the perk, if any.
   *
   * @return the expiration date or {@code null} if not set
   */
  public Calendar getExpiryDate() { return expiryDate; }

  /**
   * Sets the expiration date for the perk.
   *
   * @param expiryDate the new expiry date
   */
  public void setExpiryDate(Calendar expiryDate) { this.expiryDate = expiryDate; }

  /**
   * Returns the region this perk applies to.
   *
   * @return the applicable region or {@code null} if not region-specific
   */
  public String getRegion() { return region; }

  /**
   * Sets the region where this perk applies.
   *
   * @param region the applicable region string
   */
  public void setRegion(String region) { this.region = region; }

  /**
   * Returns the membership program providing this perk.
   *
   * @return the related {@link Membership}
   */
  public Membership getMembership() { return membership; }

  /**
   * Updates the membership associated with this perk.
   *
   * @param membership the new {@link Membership}
   */
  public void setMembership(Membership membership) { this.membership = membership; }

  /**
   * Returns the product associated with the perk.
   *
   * @return the {@link Product} this perk applies to
   */
  public Product getProduct() { return product; }

  /**
   * Updates the product associated with this perk.
   *
   * @param product the new {@link Product}
   */
  public void setProduct(Product product) { this.product = product; }

  /**
   * Returns the account that created this perk.
   *
   * @return the creating {@link Account}
   */
  public Account getCreator() { return creator; }

  /**
   * Sets the creator of this perk.
   *
   * @param creator the {@link Account} that created the perk
   */
  public void setCreator(Account creator) { this.creator = creator; }

  /**
   * Returns all accounts that have upvoted this perk.
   *
   * @return a set of {@link Account} entities
   */
  public Set<Account> getUpvotedBy() { return upvotedBy; }

  /**
   * Replaces the set of accounts who upvoted this perk.
   *
   * @param upvotedBy the new set of upvoters
   */
  public void setUpvotedBy(Set<Account> upvotedBy) { this.upvotedBy = upvotedBy; }

  /**
   * Returns all accounts that have downvoted this perk.
   *
   * @return a set of {@link Account} entities
   */
  public Set<Account> getDownvotedBy() { return downvotedBy; }

  /**
   * Replaces the set of accounts who downvoted this perk.
   *
   * @param downvotedBy the new set of downvoters
   */
  public void setDownvotedBy(Set<Account> downvotedBy) { this.downvotedBy = downvotedBy; }

  /**
   * Calculates the rating score of this perk.
   * <p>
   * Rating is defined as:
   * <pre>
   * upvotes - downvotes
   * </pre>
   *
   * @return the rating score
   */
  public int getRating() { return upvotedBy.size() - downvotedBy.size(); }

  /**
   * Returns the total number of votes (upvotes + downvotes).
   *
   * @return the total vote count
   */
  public int getTotalRatings() { return upvotedBy.size() + downvotedBy.size(); }

  /**
   * Adds an upvote from the given account.
   *
   * @param account the {@link Account} giving the upvote
   */
  public void addUpvote(Account account) { upvotedBy.add(account); }

  /**
   * Adds a downvote from the given account.
   *
   * @param account the {@link Account} giving the downvote
   */
  public void addDownvote(Account account) { downvotedBy.add(account); }

  /**
   * Removes an upvote from the given account.
   *
   * @param account the {@link Account} whose upvote should be removed
   * @return {@code true} if the upvote was removed, {@code false} otherwise
   */
  public boolean removeUpvote(Account account) { return upvotedBy.remove(account); }

  /**
   * Removes a downvote from the given account.
   *
   * @param account the {@link Account} whose downvote should be removed
   * @return {@code true} if the downvote was removed, {@code false} otherwise
   */
  public boolean removeDownvote(Account account) { return downvotedBy.remove(account); }
}
