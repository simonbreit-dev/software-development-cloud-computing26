package de.quadflal.sdfccbackend.auth;

import de.quadflal.sdfccbackend.AbstractOpenApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthOperationsContractTest extends AbstractOpenApiContractTest {

    @Test
    @DisplayName("POST /auth/login returns 200 for valid payload")
    void loginReturns200ForValidPayload() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "user@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/login returns 400 for invalid payload")
    void loginReturns400ForInvalidPayload() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "ab",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }
}
