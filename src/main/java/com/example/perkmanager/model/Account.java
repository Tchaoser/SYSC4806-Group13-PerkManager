package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user account in the Perk Manager system.
 * Accounts have unique usernames and passwords, can create perks,
 * upvote/downvote perks, save perks to their profile, and associate
 * their account with various memberships (e.g., Air Miles, CAA, Visa).
 */
@Entity
@Table(name = "accounts")
public class Account {

    /**
     * Unique identifier for the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The unique username for this account.
     */
    @NotBlank
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * The encoded password for this account.
     */
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Perks created by this account.
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Perk> perks = new HashSet<>();

    /**
     * Perks that this account has upvoted.
     */
    @ManyToMany(mappedBy = "upvotedBy")
    private Set<Perk> upvotedPerks = new HashSet<>();

    /**
     * Perks that this account has downvoted.
     */
    @ManyToMany(mappedBy = "downvotedBy")
    private Set<Perk> downvotedPerks = new HashSet<>();

    /**
     * Memberships associated with this account.
     */
    @ManyToMany
    @JoinTable(
            name = "account_memberships",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "membership_id")
    )
    private Set<Membership> memberships = new HashSet<>();

    /**
     * Perks this account has saved to its profile.
     */
    @ManyToMany
    @JoinTable(
            name = "account_saved_perks",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "perk_id")
    )
    private Set<Perk> savedPerks = new HashSet<>();

    /** Default constructor required by JPA. */
    public Account() {}

    /**
     * Returns all memberships associated with this account.
     */
    public Set<Membership> getMemberships() {
        return memberships;
    }

    /**
     * Adds a membership to the account.
     *
     * @param membership the membership to add
     */
    public void addMembership(Membership membership) {
        memberships.add(membership);
    }

    /**
     * Removes a membership from the account.
     *
     * @param membership the membership to remove
     */
    public void removeMembership(Membership membership) {
        memberships.remove(membership);
    }

    /**
     * Replaces the existing membership set.
     *
     * @param memberships updated membership set
     */
    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    /**
     * Checks whether this account has an associated membership with a given ID.
     *
     * @param id membership ID
     * @return true if the membership is linked to this account
     */
    public boolean hasMembership(Long id) {
        return memberships != null &&
                memberships.stream().anyMatch(m -> m.getId().equals(id));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Perk> getPerks() { return perks; }
    public void setPerks(Set<Perk> perks) { this.perks = perks; }

    /**
     * Adds a perk created by this account.
     *
     * @param perk perk to add
     */
    public void addPerk(Perk perk) {
        perks.add(perk);
    }

    /**
     * Removes a perk created by this account.
     *
     * @param perk perk to remove
     * @return true if removed
     */
    public boolean removePerk(Perk perk) {
        return perks.remove(perk);
    }

    /**
     * Removes a created perk by ID.
     *
     * @param id ID of the perk
     * @return removed perk, or null if not found
     */
    public Perk removePerkById(Long id) {
        return perks.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(p -> { perks.remove(p); return p; })
                .orElse(null);
    }

    public Set<Perk> getUpvotedPerks() { return upvotedPerks; }
    public Set<Perk> getDownvotedPerks() { return downvotedPerks; }

    public Set<Perk> getSavedPerks() { return savedPerks; }
    public void setSavedPerks(Set<Perk> savedPerks) { this.savedPerks = savedPerks; }

    /**
     * Adds a perk to this account's saved perk list.
     *
     * @param perk perk to save
     */
    public void addPerkToProfile(Perk perk) {
        savedPerks.add(perk);
    }

    /**
     * Removes a saved perk from the account.
     *
     * @param perk perk to remove
     */
    public void removePerkFromProfile(Perk perk) {
        savedPerks.remove(perk);
    }

    /**
     * Checks whether the account has a saved perk with the given ID.
     *
     * @param id perk ID
     * @return true if this user saved the perk
     */
    public boolean hasPerk(Long id) {
        return savedPerks != null &&
                savedPerks.stream().anyMatch(perk -> perk.getId().equals(id));
    }
}
