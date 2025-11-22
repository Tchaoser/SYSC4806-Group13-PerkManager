package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "username", unique = true, nullable = false) // Username associated with account
    private String username;

    @NotBlank
    @Column(name = "password", nullable = false) // Password associated with account
    private String password;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true) // Perks created by this account
    private Set<Perk> perks = new HashSet<>();

    @ManyToMany(mappedBy = "upvotedBy") // Upvoted perks
    private Set<Perk> upvotedPerks = new HashSet<>();

    @ManyToMany(mappedBy = "downvotedBy") // Downvoted perks
    private Set<Perk> downvotedPerks = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "account_memberships",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "membership_id")
    )
    private Set<Membership> memberships = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "account_saved_perks",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "perk_id")
    )
    private Set<Perk> savedPerks = new HashSet<>();

    public Account() {}

    public Set<Membership> getMemberships() { return memberships; }
    public void addMembership(Membership membership) { memberships.add(membership); }
    public void removeMembership(Membership membership) { memberships.remove(membership); }
    public void setMemberships(Set<Membership> memberships) { this.memberships = memberships; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Perk> getPerks() { return perks; }
    public void setPerks(Set<Perk> perks) { this.perks = perks; }

    public Set<Perk> getUpvotedPerks() { return upvotedPerks; }
    public Set<Perk> getDownvotedPerks() { return downvotedPerks; }

    public void addPerk(Perk perk) { perks.add(perk); }
    public boolean removePerk(Perk perk) { return perks.remove(perk); }

    public boolean hasPerk(Long id) {
        return savedPerks != null && savedPerks.stream().anyMatch(perk -> perk.getId().equals(id));
    }

    public Perk removePerkById(Long id) {
        return perks.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(p -> { perks.remove(p); return p; })
                .orElse(null);
    }


    public Set<Perk> getSavedPerks() { return savedPerks; }
    public void setSavedPerks(Set<Perk> savedPerks) { this.savedPerks = savedPerks; }

    public void addPerkToProfile(Perk perk) {
        savedPerks.add(perk);
    }

    public void removePerkFromProfile(Perk perk) {
        savedPerks.remove(perk);
    }
}