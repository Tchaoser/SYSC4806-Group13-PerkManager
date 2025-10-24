package com.example.perkmanager;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "perks")
public class Perk {
    @Id
    @Column(name  = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //The benefit offered by the perk. Ex: 10% next flight
    @Column(name  = "benefit")
    private String benefit;
    //The date the perk expires on (Optional)
    @Column(name = "expirydate", nullable = true)
    private Calendar expiryDate;
    //The region the perk applies to  (Optional)
    //Multiple regions should be listed in the same string
    @Column(name = "region", nullable = true)
    private String region;
    @OneToOne
    @Column(name = "membership")
    private Membership membership;
    @OneToOne
    @Column(name = "product")
    private Product product;
    @OneToMany
    @Column(name = "upvotes")
    private Set<Account> upvotes;
    @OneToMany
    @Column(name = "downvotes")
    private Set<Account> downvotes;


    public Perk() {
        upvotes = new HashSet<>();
        downvotes = new HashSet<>();
    }

    public Perk(Membership membership, Product product, String Benefit) {
        this.membership = membership;
        this.product = product;
        this.benefit = Benefit;
        upvotes = new HashSet<>();
        downvotes = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Calendar date) {
        this.expiryDate = date;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getUpvotes() {
        return upvotes.size();
    }

    public Set<Account> getUpvoteList() {
        return upvotes;
    }

    public void setUpvoteList(Set<Account> upvoteList) {
        this.upvotes = upvoteList;
    }

    public int getDownvotes() {
        return downvotes.size();
    }

    public Set<Account> getDownvoteList() {
        return downvotes;
    }

    public void setDownvoteList(Set<Account> downvoteList) {
        this.downvotes = downvoteList;
    }

    public int getRating() {
        return upvotes.size() - downvotes.size();
    }

    public int getTotalRatings() {
        return upvotes.size() + downvotes.size();
    }

    public void addUpvote(Account account) {
        upvotes.add(account);
    }

    public void addDownvote(Account account) {
        downvotes.add(account);
    }

    public boolean removeUpvote(Account account) {
        return upvotes.remove(account);
    }

    public boolean removeDownvote(Account account) {
        return downvotes.remove(account);
    }

}
