package de.quadflal.sdfccbackend.auth;

import de.quadflal.sdfccbackend.AbstractOpenApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends AbstractOpenApiContractTest {

    @Test
    @DisplayName("POST /auth/login returns 401 for invalid credentials")
    void loginReturns401ForInvalidCredentials() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "api-user",
                                  "password": "invalid-password"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/me returns 401 for invalid bearer token")
    void usersMeReturns401ForInvalidToken() throws Exception {
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/me returns 200 for valid bearer token")
    void usersMeReturns200ForValidToken() throws Exception {
        String token = obtainAccessToken();

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /unknown is denied by default")
    void unknownRouteIsDeniedByDefault() throws Exception {
        mockMvc.perform(get("/unknown"))
                .andExpect(status().isUnauthorized());
    }

    private String obtainAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "api-user",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        return responseBody.split("\"accessToken\":\"")[1].split("\"")[0];
    }
}
