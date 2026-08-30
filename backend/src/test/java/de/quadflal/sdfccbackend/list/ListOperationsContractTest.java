package de.quadflal.sdfccbackend.list;

import de.quadflal.sdfccbackend.AbstractOpenApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
                        .with(user("demo-user").roles("USER"))
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
    @DisplayName("GET /lists/{id} returns 404 for unknown list")
    void getListByIdReturns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/lists/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
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
    @DisplayName("PATCH /lists/{id} returns 404 for unknown list")
    void updateListReturns404ForUnknownId() throws Exception {
        mockMvc.perform(patch("/lists/{id}", UUID.randomUUID())
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Updated List"
                                }
                                """))
                .andExpect(status().isNotFound());
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
        // Use a dedicated list for deletion so the shared RESOURCE_ID fixture stays intact for other tests.
        String response = mockMvc.perform(post("/lists")
                        .with(user("demo-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "List to delete"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/lists/{id}", id)
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /lists/{id} returns 404 for unknown list")
    void deleteListReturns404ForUnknownId() throws Exception {
        mockMvc.perform(delete("/lists/{id}", UUID.randomUUID())
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNotFound());
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
    @DisplayName("POST /lists/{id}/restaurants returns 404 for unknown list")
    void addRestaurantToListReturns404ForUnknownListId() throws Exception {
        mockMvc.perform(post("/lists/{id}/restaurants", UUID.randomUUID())
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "restaurantId": "%s"
                                }
                                """.formatted(RESTAURANT_ID)))
                .andExpect(status().isNotFound());
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

    @Test
    @DisplayName("DELETE /lists/{id}/restaurants/{restaurantId} returns 404 for unknown list")
    void removeRestaurantFromListReturns404ForUnknownListId() throws Exception {
        mockMvc.perform(delete("/lists/{id}/restaurants/{restaurantId}", UUID.randomUUID(), RESTAURANT_ID)
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNotFound());
    }
}
