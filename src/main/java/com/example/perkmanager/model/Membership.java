package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "type", nullable = false) //What type of membership it is. Ex: Credit card, Air Miles, etc
    private String type;

    @NotBlank
    @Column(name = "org_name", nullable = false) //The Company or Organization associated with the membership. Ex: RBC, WestJet, Cineplex, etc
    private String organizationName;

    @NotBlank
    @Column(name = "description", nullable = false) //The Name/Description of the membership. Ex: West Jet Rewards Member
    private String description;

    public Membership() {}

    public Membership(String type, String organizationName, String description) {
        this.type = type;
        this.organizationName = organizationName;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
