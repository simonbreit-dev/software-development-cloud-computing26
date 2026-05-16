package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Restaurant
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class Restaurant implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;

  private String name;

  private @Nullable String description = null;

  private @Nullable String street = null;

  private @Nullable String city = null;

  private @Nullable String postalCode = null;

  private @Nullable String country = null;

  private @Nullable Double latitude = null;

  private @Nullable Double longitude = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  public Restaurant() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Restaurant(UUID id, String name, OffsetDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

  public Restaurant id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull @Valid 
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(UUID id) {
    this.id = id;
  }

  public Restaurant name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull @Size(min = 1, max = 255) 
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  public Restaurant description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public Restaurant street(@Nullable String street) {
    this.street = street;
    return this;
  }

  /**
   * Get street
   * @return street
   */
  
  @JsonProperty("street")
  public @Nullable String getStreet() {
    return street;
  }

  @JsonProperty("street")
  public void setStreet(@Nullable String street) {
    this.street = street;
  }

  public Restaurant city(@Nullable String city) {
    this.city = city;
    return this;
  }

  /**
   * Get city
   * @return city
   */
  
  @JsonProperty("city")
  public @Nullable String getCity() {
    return city;
  }

  @JsonProperty("city")
  public void setCity(@Nullable String city) {
    this.city = city;
  }

  public Restaurant postalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  
  @JsonProperty("postalCode")
  public @Nullable String getPostalCode() {
    return postalCode;
  }

  @JsonProperty("postalCode")
  public void setPostalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
  }

  public Restaurant country(@Nullable String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  
  @JsonProperty("country")
  public @Nullable String getCountry() {
    return country;
  }

  @JsonProperty("country")
  public void setCountry(@Nullable String country) {
    this.country = country;
  }

  public Restaurant latitude(@Nullable Double latitude) {
    this.latitude = latitude;
    return this;
  }

  /**
   * Get latitude
   * minimum: -90
   * maximum: 90
   * @return latitude
   */
  @DecimalMin(value = "-90") @DecimalMax(value = "90") 
  @JsonProperty("latitude")
  public @Nullable Double getLatitude() {
    return latitude;
  }

  @JsonProperty("latitude")
  public void setLatitude(@Nullable Double latitude) {
    this.latitude = latitude;
  }

  public Restaurant longitude(@Nullable Double longitude) {
    this.longitude = longitude;
    return this;
  }

  /**
   * Get longitude
   * minimum: -180
   * maximum: 180
   * @return longitude
   */
  @DecimalMin(value = "-180") @DecimalMax(value = "180") 
  @JsonProperty("longitude")
  public @Nullable Double getLongitude() {
    return longitude;
  }

  @JsonProperty("longitude")
  public void setLongitude(@Nullable Double longitude) {
    this.longitude = longitude;
  }

  public Restaurant createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("createdAt")
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Restaurant restaurant = (Restaurant) o;
    return Objects.equals(this.id, restaurant.id) &&
        Objects.equals(this.name, restaurant.name) &&
        Objects.equals(this.description, restaurant.description) &&
        Objects.equals(this.street, restaurant.street) &&
        Objects.equals(this.city, restaurant.city) &&
        Objects.equals(this.postalCode, restaurant.postalCode) &&
        Objects.equals(this.country, restaurant.country) &&
        Objects.equals(this.latitude, restaurant.latitude) &&
        Objects.equals(this.longitude, restaurant.longitude) &&
        Objects.equals(this.createdAt, restaurant.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, street, city, postalCode, country, latitude, longitude, createdAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Restaurant {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    street: ").append(toIndentedString(street)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

