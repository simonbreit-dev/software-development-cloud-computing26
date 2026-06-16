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
 * RestaurantList
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class RestaurantList implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;

  private String name;

  private @Nullable String description = null;

  private UUID ownerId;

  private Integer restaurantCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime updatedAt = null;

  public RestaurantList() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RestaurantList(UUID id, String name, UUID ownerId, Integer restaurantCount, OffsetDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.ownerId = ownerId;
    this.restaurantCount = restaurantCount;
    this.createdAt = createdAt;
  }

  public RestaurantList id(UUID id) {
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

  public RestaurantList name(String name) {
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

  public RestaurantList description(@Nullable String description) {
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

  public RestaurantList ownerId(UUID ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  /**
   * UUID of the owning user. Always the single configured user in this deployment.
   * @return ownerId
   */
  @NotNull @Valid 
  @JsonProperty("ownerId")
  public UUID getOwnerId() {
    return ownerId;
  }

  @JsonProperty("ownerId")
  public void setOwnerId(UUID ownerId) {
    this.ownerId = ownerId;
  }

  public RestaurantList restaurantCount(Integer restaurantCount) {
    this.restaurantCount = restaurantCount;
    return this;
  }

  /**
   * Get restaurantCount
   * minimum: 0
   * @return restaurantCount
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("restaurantCount")
  public Integer getRestaurantCount() {
    return restaurantCount;
  }

  @JsonProperty("restaurantCount")
  public void setRestaurantCount(Integer restaurantCount) {
    this.restaurantCount = restaurantCount;
  }

  public RestaurantList createdAt(OffsetDateTime createdAt) {
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

  public RestaurantList updatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @Valid 
  @JsonProperty("updatedAt")
  public @Nullable OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updatedAt")
  public void setUpdatedAt(@Nullable OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestaurantList restaurantList = (RestaurantList) o;
    return Objects.equals(this.id, restaurantList.id) &&
        Objects.equals(this.name, restaurantList.name) &&
        Objects.equals(this.description, restaurantList.description) &&
        Objects.equals(this.ownerId, restaurantList.ownerId) &&
        Objects.equals(this.restaurantCount, restaurantList.restaurantCount) &&
        Objects.equals(this.createdAt, restaurantList.createdAt) &&
        Objects.equals(this.updatedAt, restaurantList.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, ownerId, restaurantCount, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestaurantList {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    ownerId: ").append(toIndentedString(ownerId)).append("\n");
    sb.append("    restaurantCount: ").append(toIndentedString(restaurantCount)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
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

