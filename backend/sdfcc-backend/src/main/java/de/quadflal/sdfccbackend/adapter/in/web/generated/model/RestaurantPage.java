package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RestaurantPage
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class RestaurantPage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid RestaurantResponse> content = new ArrayList<>();

  private PageMetadata page;

  public RestaurantPage() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RestaurantPage(List<@Valid RestaurantResponse> content, PageMetadata page) {
    this.content = content;
    this.page = page;
  }

  public RestaurantPage content(List<@Valid RestaurantResponse> content) {
    this.content = content;
    return this;
  }

  public RestaurantPage addContentItem(RestaurantResponse contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }
    this.content.add(contentItem);
    return this;
  }

  /**
   * Get content
   * @return content
   */
  @NotNull @Valid 
  @JsonProperty("content")
  public List<@Valid RestaurantResponse> getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(List<@Valid RestaurantResponse> content) {
    this.content = content;
  }

  public RestaurantPage page(PageMetadata page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  @NotNull @Valid 
  @JsonProperty("page")
  public PageMetadata getPage() {
    return page;
  }

  @JsonProperty("page")
  public void setPage(PageMetadata page) {
    this.page = page;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestaurantPage restaurantPage = (RestaurantPage) o;
    return Objects.equals(this.content, restaurantPage.content) &&
        Objects.equals(this.page, restaurantPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestaurantPage {\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

