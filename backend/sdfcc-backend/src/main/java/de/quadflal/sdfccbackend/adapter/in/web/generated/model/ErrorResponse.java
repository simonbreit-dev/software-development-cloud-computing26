package de.quadflal.sdfccbackend.adapter.in.web.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.FieldError;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * RFC 9457 Problem Details response. The &#x60;type&#x60; URI should resolve to human-readable documentation describing the problem type. The optional &#x60;errors&#x60; extension carries field-level validation failures. 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class ErrorResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private URI type;

  private String title;

  private Integer status;

  private @Nullable String detail;

  private @Nullable URI instance;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime timestamp;

  @Valid
  private List<@Valid FieldError> errors = new ArrayList<>();

  public ErrorResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ErrorResponse(URI type, String title, Integer status) {
    this.type = type;
    this.title = title;
    this.status = status;
  }

  public ErrorResponse type(URI type) {
    this.type = type;
    return this;
  }

  /**
   * A URI reference that identifies the problem type. When dereferenced, it SHOULD provide human-readable documentation for the problem. 
   * @return type
   */
  @NotNull @Valid 
  @JsonProperty("type")
  public URI getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(URI type) {
    this.type = type;
  }

  public ErrorResponse title(String title) {
    this.title = title;
    return this;
  }

  /**
   * A short, human-readable summary of the problem type.
   * @return title
   */
  @NotNull 
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  public ErrorResponse status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * The HTTP status code for this occurrence of the problem.
   * @return status
   */
  @NotNull 
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(Integer status) {
    this.status = status;
  }

  public ErrorResponse detail(@Nullable String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * A human-readable explanation specific to this occurrence.
   * @return detail
   */
  
  @JsonProperty("detail")
  public @Nullable String getDetail() {
    return detail;
  }

  @JsonProperty("detail")
  public void setDetail(@Nullable String detail) {
    this.detail = detail;
  }

  public ErrorResponse instance(@Nullable URI instance) {
    this.instance = instance;
    return this;
  }

  /**
   * A URI reference identifying the specific occurrence of the problem.
   * @return instance
   */
  @Valid 
  @JsonProperty("instance")
  public @Nullable URI getInstance() {
    return instance;
  }

  @JsonProperty("instance")
  public void setInstance(@Nullable URI instance) {
    this.instance = instance;
  }

  public ErrorResponse timestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * The date and time at which the problem occurred (extension field).
   * @return timestamp
   */
  @Valid 
  @JsonProperty("timestamp")
  public @Nullable OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @JsonProperty("timestamp")
  public void setTimestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public ErrorResponse errors(List<@Valid FieldError> errors) {
    this.errors = errors;
    return this;
  }

  public ErrorResponse addErrorsItem(FieldError errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }

  /**
   * Optional list of field-level validation errors (extension field).
   * @return errors
   */
  @Valid 
  @JsonProperty("errors")
  public List<@Valid FieldError> getErrors() {
    return errors;
  }

  @JsonProperty("errors")
  public void setErrors(List<@Valid FieldError> errors) {
    this.errors = errors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.type, errorResponse.type) &&
        Objects.equals(this.title, errorResponse.title) &&
        Objects.equals(this.status, errorResponse.status) &&
        Objects.equals(this.detail, errorResponse.detail) &&
        Objects.equals(this.instance, errorResponse.instance) &&
        Objects.equals(this.timestamp, errorResponse.timestamp) &&
        Objects.equals(this.errors, errorResponse.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, status, detail, instance, timestamp, errors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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

