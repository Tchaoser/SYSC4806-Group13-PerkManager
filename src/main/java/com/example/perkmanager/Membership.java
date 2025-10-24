package com.example.perkmanager;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a certain membership
 */
@Entity
public class Membership {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    //What type of membership it is. Ex: Credit card, Air Miles, etc
    @Column(name = "type")
    private String type;
    //The Company or Organization associated with the membership. Ex: RBC, WestJet, Cineplex, etc
    @Column(name = "orgName")
    private String organizationName;
    //The Name/Description of the membership. Ex: West Jet Rewards Member
    @Column(name = "description")
    private String description;

    public Membership() {
    }

    public Membership(String type, String organizationName, String description) {
        this.type = type;
        this.organizationName = organizationName;
        this.description = description;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getOrganizationName(){
        return organizationName;
    }

    public void setOrganizationName(String organizationName){
        this.organizationName = organizationName;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
