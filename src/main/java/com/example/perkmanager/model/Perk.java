package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perks")
public class Perk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "benefit", nullable = false) //The benefit offered by the perk. Ex: 10% next flight
    private String benefit;

    @FutureOrPresent(message = "Date must not have already passed")
    @Column(name = "expiry_date", nullable = true) //The date the perk expires on (Optional)
    private Calendar expiryDate;

    @Column(name = "region", nullable = true) //The region the perk applies to  (Optional). Multiple regions should be listed in the same string
    private String region;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Account creator;

    // Accounts who upvoted
    @ManyToMany
    @JoinTable(
            name = "perk_upvotes",
            joinColumns = @JoinColumn(name = "perk_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> upvotedBy = new HashSet<>();

    // Accounts who downvoted
    @ManyToMany
    @JoinTable(
            name = "perk_downvotes",
            joinColumns = @JoinColumn(name = "perk_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> downvotedBy = new HashSet<>();

    public Perk() {}

    public Perk(Membership membership, Product product, String benefit) {
        this.membership = membership;
        this.product = product;
        this.benefit = benefit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBenefit() { return benefit; }
    public void setBenefit(String benefit) { this.benefit = benefit; }

    public Calendar getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Calendar expiryDate) { this.expiryDate = expiryDate; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Account getCreator() { return creator; }
    public void setCreator(Account creator) { this.creator = creator; }

    public Set<Account> getUpvotedBy() { return upvotedBy; }
    public void setUpvotedBy(Set<Account> upvotedBy) { this.upvotedBy = upvotedBy; }

    public Set<Account> getDownvotedBy() { return downvotedBy; }
    public void setDownvotedBy(Set<Account> downvotedBy) { this.downvotedBy = downvotedBy; }

    public int getRating() { return upvotedBy.size() - downvotedBy.size(); }
    public int getTotalRatings() { return upvotedBy.size() + downvotedBy.size(); }

    public void addUpvote(Account account) { upvotedBy.add(account); }
    public void addDownvote(Account account) { downvotedBy.add(account); }

    public boolean removeUpvote(Account account) { return upvotedBy.remove(account); }
    public boolean removeDownvote(Account account) { return downvotedBy.remove(account); }
}
