package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user account in the Perk Manager system.
 * Each account has a unique username, password, and can be associated with multiple memberships.
 * Accounts can create perks, upvote/downvote perks, and manage their membership associations.
 *
 */
@Entity
@Table(name = "accounts")
public class Account {
  /**
   * The unique identifier for the account.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * The unique username associated with the account.
   */
  @NotBlank
  @Column(name = "username", unique = true, nullable = false) // Username associated with account
  private String username;

  /**
   * The password associated with the account.
   */
  @NotBlank
  @Column(name = "password", nullable = false) // Password associated with account
  private String password;

  /**
   * The set of perks created by this account.
   */
  @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true) // Perks created by this account
  private Set<Perk> perks = new HashSet<>();

  /**
   * The set of perks that this account has upvoted.
   */
  @ManyToMany(mappedBy = "upvotedBy") // Upvoted perks
  private Set<Perk> upvotedPerks = new HashSet<>();

  /**
   * The set of perks that this account has downvoted.
   */
  @ManyToMany(mappedBy = "downvotedBy") // Downvoted perks
  private Set<Perk> downvotedPerks = new HashSet<>();


  /**
   * The set of memberships associated with this account (e.g., Air Miles, CAA, Visa).
   */
  @ManyToMany
  @JoinTable(
    name = "account_memberships",
    joinColumns = @JoinColumn(name = "account_id"),
    inverseJoinColumns = @JoinColumn(name = "membership_id")
  )
  private Set<Membership> memberships = new HashSet<>();

  /**
   * The set of perks saved by this account
   */
  @ManyToMany
  @JoinTable(
    name = "account_saved_perks",
    joinColumns = @JoinColumn(name = "account_id"),
    inverseJoinColumns = @JoinColumn(name = "perk_id")
  )
  private Set<Perk> savedPerks = new HashSet<>();

  /**
   * Default constructor for JPA.
   */
  public Account() {}

  /**
   * Returns the set of memberships associated with this account.
   *
   * @return a set of {@link Membership} entities linked to the account
   */
  public Set<Membership> getMemberships() { return memberships; }

  /**
   * Adds the specified membership to the account.
   *
   * @param membership the {@link Membership} to associate with this account
   */
  public void addMembership(Membership membership) { memberships.add(membership); }

  /**
   * Removes the specified membership from the account.
   *
   * @param membership the {@link Membership} to disassociate from this account
   */
  public void removeMembership(Membership membership) { memberships.remove(membership); }

  /**
   * Replaces the current membership set with the provided one.
   *
   * @param memberships the new set of {@link Membership} entities
   */
  public void setMemberships(Set<Membership> memberships) { this.memberships = memberships; }


  /**
   * Returns the unique identifier of this account.
   *
   * @return the account ID
   */
  public Long getId() { return id; }

  /**
   * Sets the unique identifier for this account.
   *
   * @param id the new account ID
   */
  public void setId(Long id) { this.id = id; }


  /**
   * Returns the username associated with this account.
   *
   * @return the account's username
   */
  public String getUsername() { return username; }

  /**
   * Updates the username for this account.
   *
   * @param username the new username
   */
  public void setUsername(String username) { this.username = username; }


  /**
   * Returns the password for this account.
   *
   * @return the hashed or encoded password value
   */
  public String getPassword() { return password; }

  /**
   * Sets the password for this account.
   *
   * @param password the new hashed or encoded password value
   */
  public void setPassword(String password) { this.password = password; }

  /**
   * Returns all perks created by this account.
   *
   * @return a set of {@link Perk} entities created by the user
   */
  public Set<Perk> getPerks() { return perks; }
  /**
   * Replaces all perks created by this account.
   *
   * @param perks the new set of created perks
   */
  public void setPerks(Set<Perk> perks) { this.perks = perks; }

  /**
   * Returns the set of perks this account has upvoted.
   *
   * @return a set of upvoted {@link Perk} entities
   */
  public Set<Perk> getUpvotedPerks() { return upvotedPerks; }
  /**
   * Returns the set of perks this account has downvoted.
   *
   * @return a set of downvoted {@link Perk} entities
   */
  public Set<Perk> getDownvotedPerks() { return downvotedPerks; }

  /**
   * Associates a new perk with this account as its creator.
   *
   * @param perk the {@link Perk} to add
   */
  public void addPerk(Perk perk) { perks.add(perk); }
  /**
   * Removes a perk created by this account.
   *
   * @param perk the {@link Perk} to remove
   * @return {@code true} if the perk was successfully removed, otherwise {@code false}
   */
  public boolean removePerk(Perk perk) { return perks.remove(perk); }

  /**
   * Checks whether the account has a saved perk with the specified ID.
   *
   * @param id the ID of the perk to check
   * @return {@code true} if the perk is saved by this account, otherwise {@code false}
   */
  public boolean hasPerk(Long id) {
    return savedPerks != null && savedPerks.stream().anyMatch(perk -> perk.getId().equals(id));
  }

  /**
   * Removes a perk created by this account based on its ID.
   *
   * @param id the ID of the {@link Perk} to remove
   * @return the removed {@link Perk}, or {@code null} if no matching perk was found
   */
  public Perk removePerkById(Long id) {
    return perks.stream()
      .filter(p -> p.getId().equals(id))
      .findFirst()
      .map(p -> { perks.remove(p); return p; })
      .orElse(null);
  }

  /**
   * Returns the set of perks saved to this user's profile.
   *
   * @return a set of saved {@link Perk} entities
   */
  public Set<Perk> getSavedPerks() { return savedPerks; }
  /**
   * Replaces the current set of saved perks for this account.
   *
   * @param savedPerks the new set of saved perks
   */
  public void setSavedPerks(Set<Perk> savedPerks) { this.savedPerks = savedPerks; }

  /**
   * Adds a perk to the user's saved perks list.
   *
   * @param perk the {@link Perk} to save to the profile
   */
  public void addPerkToProfile(Perk perk) {
    savedPerks.add(perk);
  }

  /**
   * Removes a perk from the user's saved perks list.
   *
   * @param perk the {@link Perk} to remove from the profile
   */
  public void removePerkFromProfile(Perk perk) {
    savedPerks.remove(perk);
  }
}