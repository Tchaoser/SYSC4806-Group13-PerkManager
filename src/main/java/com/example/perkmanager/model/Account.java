package com.example.perkmanager.model;

import jakarta.persistence.*;
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
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * The password associated with the account.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The set of perks created by this account.
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Perk> perks = new HashSet<>();

    /**
     * The set of perks that this account has upvoted.
     */
    @ManyToMany(mappedBy = "upvotedBy")
    private Set<Perk> upvotedPerks = new HashSet<>();

    /**
     * The set of perks that this account has downvoted.
     */
    @ManyToMany(mappedBy = "downvotedBy")
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
     * Default constructor for JPA.
     */
    public Account() {}

    /**
     * Gets the set of memberships associated with this account.
     *
     * @return the set of memberships
     */
    public Set<Membership> getMemberships() { return memberships; }

    /**
     * Adds a membership to this account.
     *
     * @param membership the membership to add
     */
    public void addMembership(Membership membership) { memberships.add(membership); }

    /**
     * Removes a membership from this account.
     *
     * @param membership the membership to remove
     */
    public void removeMembership(Membership membership) { memberships.remove(membership); }

    /**
     * Gets the unique identifier of this account.
     *
     * @return the account ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier of this account.
     *
     * @param id the account ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the username of this account.
     *
     * @return the username
     */
    //TODO: check for username uniqueness in database
    public String getUsername() { return username; }

    /**
     * Sets the username of this account.
     *
     * @param username the username to set
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the password of this account.
     *
     * @return the password
     */
    public String getPassword() { return password; }

    /**
     * Sets the password of this account.
     *
     * @param password the password to set
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Gets the set of perks created by this account.
     *
     * @return the set of created perks
     */
    public Set<Perk> getPerks() { return perks; }

    /**
     * Sets the set of perks created by this account.
     *
     * @param perks the set of perks to set
     */
    public void setPerks(Set<Perk> perks) { this.perks = perks; }

    /**
     * Gets the set of perks that this account has upvoted.
     *
     * @return the set of upvoted perks
     */
    public Set<Perk> getUpvotedPerks() { return upvotedPerks; }

    /**
     * Gets the set of perks that this account has downvoted.
     *
     * @return the set of downvoted perks
     */
    public Set<Perk> getDownvotedPerks() { return downvotedPerks; }

    /**
     * Adds a perk to the set of perks created by this account.
     *
     * @param perk the perk to add
     */
    public void addPerk(Perk perk) { perks.add(perk); }

    /**
     * Removes a perk from the set of perks created by this account.
     *
     * @param perk the perk to remove
     * @return true if the perk was removed, false otherwise
     */
    public boolean removePerk(Perk perk) { return perks.remove(perk); }

    /**
     * Removes a perk by its ID from the set of perks created by this account.
     *
     * @param id the ID of the perk to remove
     * @return the removed perk, or null if not found
     */
    public Perk removePerkById(Long id) {
        return perks.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(p -> { perks.remove(p); return p; })
                .orElse(null);
    }


}