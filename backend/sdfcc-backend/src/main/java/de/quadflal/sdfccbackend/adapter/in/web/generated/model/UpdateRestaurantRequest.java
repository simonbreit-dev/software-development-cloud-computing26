package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Partial restaurant update payload
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class UpdateRestaurantRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private @Nullable String name;

  private @Nullable String description;

  private @Nullable String street;

  private @Nullable String city;

  private @Nullable String postalCode;

  private @Nullable String country;

  private @Nullable Double latitude;

  private @Nullable Double longitude;

  public UpdateRestaurantRequest name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @Size(min = 1, max = 255) 
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(@Nullable String name) {
    this.name = name;
  }

  public UpdateRestaurantRequest description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  @Size(max = 2000) 
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public UpdateRestaurantRequest street(@Nullable String street) {
    this.street = street;
    return this;
  }

  /**
   * Get street
   * @return street
   */
  @Size(max = 255) 
  @JsonProperty("street")
  public @Nullable String getStreet() {
    return street;
  }

  @JsonProperty("street")
  public void setStreet(@Nullable String street) {
    this.street = street;
  }

  public UpdateRestaurantRequest city(@Nullable String city) {
    this.city = city;
    return this;
  }

  /**
   * Get city
   * @return city
   */
  @Size(max = 255) 
  @JsonProperty("city")
  public @Nullable String getCity() {
    return city;
  }

  @JsonProperty("city")
  public void setCity(@Nullable String city) {
    this.city = city;
  }

  public UpdateRestaurantRequest postalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  @Size(max = 20) 
  @JsonProperty("postalCode")
  public @Nullable String getPostalCode() {
    return postalCode;
  }

  @JsonProperty("postalCode")
  public void setPostalCode(@Nullable String postalCode) {
    this.postalCode = postalCode;
  }

  public UpdateRestaurantRequest country(@Nullable String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  @Size(max = 100) 
  @JsonProperty("country")
  public @Nullable String getCountry() {
    return country;
  }

  @JsonProperty("country")
  public void setCountry(@Nullable String country) {
    this.country = country;
  }

  public UpdateRestaurantRequest latitude(@Nullable Double latitude) {
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

  public UpdateRestaurantRequest longitude(@Nullable Double longitude) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateRestaurantRequest updateRestaurantRequest = (UpdateRestaurantRequest) o;
    return Objects.equals(this.name, updateRestaurantRequest.name) &&
        Objects.equals(this.description, updateRestaurantRequest.description) &&
        Objects.equals(this.street, updateRestaurantRequest.street) &&
        Objects.equals(this.city, updateRestaurantRequest.city) &&
        Objects.equals(this.postalCode, updateRestaurantRequest.postalCode) &&
        Objects.equals(this.country, updateRestaurantRequest.country) &&
        Objects.equals(this.latitude, updateRestaurantRequest.latitude) &&
        Objects.equals(this.longitude, updateRestaurantRequest.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, street, city, postalCode, country, latitude, longitude);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateRestaurantRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    street: ").append(toIndentedString(street)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
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

