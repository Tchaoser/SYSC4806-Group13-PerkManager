package com.example.perkmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a membership in the Perk Manager system.
 * Memberships can be associated with accounts and are required for certain perks.
 * Examples include credit cards (Visa, Mastercard), loyalty programs (Air Miles, CAA),
 * and organization memberships (WestJet Rewards, Cineplex).
 *
 */
@Entity
@Table(name = "memberships")
public class Membership {

  public static final int TYPE_MAX_LENGTH = 100;
  public static final int ORG_NAME_MAX_LENGTH = 100;
  public static final int DESCRIPTION_MAX_LENGTH = 500;

  /**
   * The unique identifier for the membership.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  /**
   * The type of membership (e.g., Credit card, Air Miles, Loyalty program).
   */
  @NotBlank
  @Column(name = "type", nullable = false) //What type of membership it is. Ex: Credit card, Air Miles, etc
  private String type;

  /**
   * The company or organization associated with the membership (e.g., RBC, WestJet, Cineplex).
   */
  @NotBlank
  @Column(name = "org_name", nullable = false) //The Company or Organization associated with the membership. Ex: RBC, WestJet, Cineplex, etc
  private String organizationName;

  /**
   * The name or description of the membership (e.g., West Jet Rewards Member).
   */
  @NotBlank
  @Column(name = "description", nullable = false) //The Name/Description of the membership. Ex: West Jet Rewards Member
  private String description;

  /**
   * Default constructor for JPA.
   */
  public Membership() {}

  /**
   * Constructs a new Membership with the specified details.
   *
   * @param type the type of membership
   * @param organizationName the organization or company name
   * @param description the description of the membership
   */
  public Membership(String type, String organizationName, String description) {
    this.type = type;
    this.organizationName = organizationName;
    this.description = description;
  }

  /**
   * Gets the unique identifier of this membership.
   *
   * @return the membership ID
   */
  public Long getId() { return id; }

  /**
   * Sets the unique identifier of this membership.
   *
   * @param id the membership ID
   */
  public void setId(Long id) { this.id = id; }

  /**
   * Gets the type of this membership.
   *
   * @return the membership type
   */
  public String getType() { return type; }

  /**
   * Sets the type of this membership.
   *
   * @param type the membership type to set
   */
  public void setType(String type) { this.type = type; }

  /**
   * Gets the organization name associated with this membership.
   *
   * @return the organization name
   */
  public String getOrganizationName() { return organizationName; }

  /**
   * Sets the organization name associated with this membership.
   *
   * @param organizationName the organization name to set
   */
  public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

  /**
   * Gets the description of this membership.
   *
   * @return the membership description
   */
  public String getDescription() { return description; }

  /**
   * Sets the description of this membership.
   *
   * @param description the membership description to set
   */
  public void setDescription(String description) { this.description = description; }
}
