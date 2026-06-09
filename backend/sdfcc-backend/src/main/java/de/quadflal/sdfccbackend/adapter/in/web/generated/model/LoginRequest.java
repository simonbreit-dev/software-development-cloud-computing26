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
 * LoginRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.22.0")
public class LoginRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String usernameOrEmail;

  private String password;

  public LoginRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LoginRequest(String usernameOrEmail, String password) {
    this.usernameOrEmail = usernameOrEmail;
    this.password = password;
  }

  public LoginRequest usernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
    return this;
  }

  /**
   * Get usernameOrEmail
   * @return usernameOrEmail
   */
  @NotNull @Size(min = 3, max = 255) 
  @JsonProperty("usernameOrEmail")
  public String getUsernameOrEmail() {
    return usernameOrEmail;
  }

  @JsonProperty("usernameOrEmail")
  public void setUsernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }

  public LoginRequest password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
   */
  @NotNull @Size(min = 8, max = 128) 
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  @JsonProperty("password")
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginRequest loginRequest = (LoginRequest) o;
    return Objects.equals(this.usernameOrEmail, loginRequest.usernameOrEmail) &&
        Objects.equals(this.password, loginRequest.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(usernameOrEmail, password);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginRequest {\n");
    sb.append("    usernameOrEmail: ").append(toIndentedString(usernameOrEmail)).append("\n");
    sb.append("    password: ").append("*").append("\n");
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

