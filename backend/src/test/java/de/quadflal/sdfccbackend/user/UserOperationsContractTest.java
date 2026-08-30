package de.quadflal.sdfccbackend.user;

import de.quadflal.sdfccbackend.AbstractOpenApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserOperationsContractTest extends AbstractOpenApiContractTest {

    @Test
    @DisplayName("GET /users/me requires authentication")
    void getCurrentUserReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /users/me returns the persisted authenticated user")
    void getCurrentUserReturns200WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/users/me")
                        .with(user("demo-user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("demo-user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}
