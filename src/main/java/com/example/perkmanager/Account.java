package com.example.perkmanager;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //Username associated with account
    @Column(name = "username", unique = true, nullable = false)
    private String Username;
    //Password associated with account
    @Column(name = "password", nullable = false)
    private String Password;
    @OneToMany
    @Column(name = "perks")
    private Set<Perk> perks;

    public Account() {
        perks = new HashSet<>();
    }

    public Account(String username, String password) {
        this.setUsername(username);
        Password = password;
        perks = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    //TODO Check database for uniqueness when setting username
    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isCorrectPassword(String password) {
        return Password.equals(password);
    }

    public void setPerks(Set<Perk> perks) {
        this.perks = perks;
    }

    public Set<Perk> getPerks() {
        return perks;
    }


    public void addPerk(Perk perk) {
        perks.add(perk);
    }

    public boolean removePerk(Perk perk) {
        return perks.remove(perk);
    }

    /**
     * Remove perk with given id
     *
     * @param id The id of the perk to be removed
     * @return The removed perk
     */
    public Perk removePerk(Long id) {
        Perk temp = null;
        for (Perk perk : perks) {
            if (perk.getId().equals(id)) {
                temp = perk;
                perks.remove(perk);
                return temp;
            }
        }
        return temp;
    }


}
