package com.example.perkmanager;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "perks")
public class Perk {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //The benefit offered by the perk. Ex: 10% next flight
    private String benefit;
    //The date the perk expires on (Optional)
    @Column(nullable = true)
    private Calendar expiryDate;
    //The region the perk applies to  (Optional)
    //Multiple regions should be listed in the same string
    @Column(nullable = true)
    private String region;
    @OneToOne
    private Membership membership;
    @OneToOne
    private Product product;
    @OneToMany
    private List<Account> upvotes;
    @OneToMany
    private  List<Account> downvotes;


    public Perk() {
        upvotes = new ArrayList<>();
        downvotes = new ArrayList<>();
    }

    public Perk(Membership membership, Product product, String Benefit) {
        this.membership = membership;
        this.product = product;
        this.benefit = Benefit;
        upvotes = new ArrayList<>();
        downvotes = new ArrayList<>();
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

    public List<Account> getUpvoteList() {
        return upvotes;
    }

    public void setUpvoteList(List<Account> upvoteList) {
        this.upvotes = upvoteList;
    }

    public int getDownvotes() {
        return downvotes.size();
    }

    public List<Account> getDownvoteList() {
        return downvotes;
    }

    public void setDownvoteList(List<Account> downvoteList) {
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
