package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AddRestaurantToListRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class AddRestaurantToListRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID restaurantId;

  public AddRestaurantToListRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddRestaurantToListRequest(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  public AddRestaurantToListRequest restaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
    return this;
  }

  /**
   * Get restaurantId
   * @return restaurantId
   */
  @NotNull @Valid 
  @JsonProperty("restaurantId")
  public UUID getRestaurantId() {
    return restaurantId;
  }

  @JsonProperty("restaurantId")
  public void setRestaurantId(UUID restaurantId) {
    this.restaurantId = restaurantId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddRestaurantToListRequest addRestaurantToListRequest = (AddRestaurantToListRequest) o;
    return Objects.equals(this.restaurantId, addRestaurantToListRequest.restaurantId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restaurantId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddRestaurantToListRequest {\n");
    sb.append("    restaurantId: ").append(toIndentedString(restaurantId)).append("\n");
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

