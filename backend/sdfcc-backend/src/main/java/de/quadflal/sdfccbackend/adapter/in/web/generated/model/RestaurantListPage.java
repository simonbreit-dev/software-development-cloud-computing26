package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList;
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
 * RestaurantListPage
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class RestaurantListPage implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid RestaurantList> content = new ArrayList<>();

  private PageMetadata page;

  public RestaurantListPage() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RestaurantListPage(List<@Valid RestaurantList> content, PageMetadata page) {
    this.content = content;
    this.page = page;
  }

  public RestaurantListPage content(List<@Valid RestaurantList> content) {
    this.content = content;
    return this;
  }

  public RestaurantListPage addContentItem(RestaurantList contentItem) {
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
  public List<@Valid RestaurantList> getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(List<@Valid RestaurantList> content) {
    this.content = content;
  }

  public RestaurantListPage page(PageMetadata page) {
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
    RestaurantListPage restaurantListPage = (RestaurantListPage) o;
    return Objects.equals(this.content, restaurantListPage.content) &&
        Objects.equals(this.page, restaurantListPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestaurantListPage {\n");
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

