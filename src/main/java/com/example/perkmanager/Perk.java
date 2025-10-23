package com.example.perkmanager;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Perk {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    //The benefit offered by the perk. Ex: 10% next flight
    private String benefit;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
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
