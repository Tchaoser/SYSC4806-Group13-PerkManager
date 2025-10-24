package com.example.perkmanager.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username", unique = true, nullable = false) // Username associated with account
    private String username;

    @Column(name = "password", nullable = false) //Password associated with account
    private String password;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true) // Perks created by this account
    private Set<Perk> perks = new HashSet<>();

    @ManyToMany(mappedBy = "upvotedBy") // Upvoted perks
    private Set<Perk> upvotedPerks = new HashSet<>();

    @ManyToMany(mappedBy = "downvotedBy") // Downvoted perks
    private Set<Perk> downvotedPerks = new HashSet<>();

    public Account() {}

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isCorrectPassword(String password) { return this.password.equals(password); }

    public Set<Perk> getPerks() { return perks; }
    public void setPerks(Set<Perk> perks) { this.perks = perks; }

    public Set<Perk> getUpvotedPerks() { return upvotedPerks; }
    public Set<Perk> getDownvotedPerks() { return downvotedPerks; }

    public void addPerk(Perk perk) { perks.add(perk); }
    public boolean removePerk(Perk perk) { return perks.remove(perk); }

    public Perk removePerkById(Long id) {
        for (Perk perk : perks) {
            if (perk.getId().equals(id)) {
                perks.remove(perk);
                return perk;
            }
        }
        return null; // nothing found
    }

}