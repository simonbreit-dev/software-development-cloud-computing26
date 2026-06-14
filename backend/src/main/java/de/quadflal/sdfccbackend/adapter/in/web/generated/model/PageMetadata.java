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
 * PageMetadata
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class PageMetadata implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer page;

  private Integer size;

  private Long totalElements;

  private Integer totalPages;

  public PageMetadata() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PageMetadata(Integer page, Integer size, Long totalElements, Integer totalPages) {
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
  }

  public PageMetadata page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * minimum: 0
   * @return page
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  @JsonProperty("page")
  public void setPage(Integer page) {
    this.page = page;
  }

  public PageMetadata size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Get size
   * minimum: 1
   * @return size
   */
  @NotNull @Min(value = 1) 
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  @JsonProperty("size")
  public void setSize(Integer size) {
    this.size = size;
  }

  public PageMetadata totalElements(Long totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  /**
   * Get totalElements
   * minimum: 0
   * @return totalElements
   */
  @NotNull @Min(value = 0L) 
  @JsonProperty("totalElements")
  public Long getTotalElements() {
    return totalElements;
  }

  @JsonProperty("totalElements")
  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  public PageMetadata totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * minimum: 0
   * @return totalPages
   */
  @NotNull @Min(value = 0) 
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  @JsonProperty("totalPages")
  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetadata pageMetadata = (PageMetadata) o;
    return Objects.equals(this.page, pageMetadata.page) &&
        Objects.equals(this.size, pageMetadata.size) &&
        Objects.equals(this.totalElements, pageMetadata.totalElements) &&
        Objects.equals(this.totalPages, pageMetadata.totalPages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, totalElements, totalPages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageMetadata {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
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

