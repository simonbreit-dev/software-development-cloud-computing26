package de.quadflal.sdfccbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListOperationsContractTest extends AbstractOpenApiContractTest {

    @Test
    @DisplayName("GET /lists is public and returns 200")
    void listListsReturns200() throws Exception {
        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /lists requires authentication")
    void createListReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/lists")
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Top Dinner"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /lists returns 201 for authenticated user")
    void createListReturns201WhenAuthenticated() throws Exception {
        mockMvc.perform(post("/lists")
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Top Dinner",
                                  "description": "My favorites"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /lists/{id} is public and returns 200")
    void getListByIdReturns200() throws Exception {
        mockMvc.perform(get("/lists/{id}", RESOURCE_ID))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /lists/{id} requires authentication")
    void updateListReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(patch("/lists/{id}", RESOURCE_ID)
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Updated List"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /lists/{id} returns 200 for authenticated user")
    void updateListReturns200WhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/lists/{id}", RESOURCE_ID)
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Updated List"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /lists/{id} requires authentication")
    void deleteListReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/lists/{id}", RESOURCE_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /lists/{id} returns 204 for authenticated user")
    void deleteListReturns204WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/lists/{id}", RESOURCE_ID)
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /lists/{id}/restaurants is public and returns 200")
    void getRestaurantsInListReturns200() throws Exception {
        mockMvc.perform(get("/lists/{id}/restaurants", RESOURCE_ID))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /lists/{id}/restaurants requires authentication")
    void addRestaurantToListReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/lists/{id}/restaurants", RESOURCE_ID)
                        .contentType(JSON)
                        .content("""
                                {
                                  "restaurantId": "%s"
                                }
                                """.formatted(RESTAURANT_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /lists/{id}/restaurants returns 204 for authenticated user")
    void addRestaurantToListReturns204WhenAuthenticated() throws Exception {
        mockMvc.perform(post("/lists/{id}/restaurants", RESOURCE_ID)
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "restaurantId": "%s"
                                }
                                """.formatted(RESTAURANT_ID)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /lists/{id}/restaurants/{restaurantId} requires authentication")
    void removeRestaurantFromListReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/lists/{id}/restaurants/{restaurantId}", RESOURCE_ID, RESTAURANT_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /lists/{id}/restaurants/{restaurantId} returns 204 for authenticated user")
    void removeRestaurantFromListReturns204WhenAuthenticated() throws Exception {
        mockMvc.perform(delete("/lists/{id}/restaurants/{restaurantId}", RESOURCE_ID, RESTAURANT_ID)
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNoContent());
    }
}
