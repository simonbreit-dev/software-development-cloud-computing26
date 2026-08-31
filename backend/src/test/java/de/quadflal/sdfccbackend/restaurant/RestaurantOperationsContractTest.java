package de.quadflal.sdfccbackend.restaurant;

import de.quadflal.sdfccbackend.AbstractOpenApiContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestaurantOperationsContractTest extends AbstractOpenApiContractTest {

    @Test
    @DisplayName("GET /restaurants is public and returns 200")
    void listRestaurantsReturns200() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /restaurants returns 400 for invalid page size")
    void listRestaurantsReturns400ForInvalidSize() throws Exception {
        mockMvc.perform(get("/restaurants").queryParam("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }

    @Test
    @DisplayName("POST /restaurants requires authentication")
    void addRestaurantReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/restaurants")
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Cafe Europa"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /restaurants returns 201 for authenticated user")
    void addRestaurantReturns201WhenAuthenticated() throws Exception {
        mockMvc.perform(post("/restaurants")
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Cafe Europa",
                                  "description": "Great brunch",
                                  "city": "Berlin",
                                  "country": "Germany"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /restaurants respects the sort query parameter")
    void listRestaurantsRespectsSortParameter() throws Exception {
        // Use a unique search term so the sort assertion only sees these two fixtures.
        mockMvc.perform(post("/restaurants")
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "ZZZ-SortFixture"
                                }
                                """))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/restaurants")
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "AAA-SortFixture"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/restaurants")
                        .queryParam("search", "SortFixture")
                        .queryParam("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("AAA-SortFixture"))
                .andExpect(jsonPath("$.content[1].name").value("ZZZ-SortFixture"));

        mockMvc.perform(get("/restaurants")
                        .queryParam("search", "SortFixture")
                        .queryParam("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("ZZZ-SortFixture"))
                .andExpect(jsonPath("$.content[1].name").value("AAA-SortFixture"));
    }

    @Test
    @DisplayName("GET /restaurants/{id} is public and returns 200")
    void getRestaurantByIdReturns200() throws Exception {
        mockMvc.perform(get("/restaurants/{id}", RESOURCE_ID))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /restaurants/{id} requires authentication")
    void updateRestaurantReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(patch("/restaurants/{id}", RESOURCE_ID)
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Updated Name"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /restaurants/{id} returns 200 for authenticated user")
    void updateRestaurantReturns200WhenAuthenticated() throws Exception {
        mockMvc.perform(patch("/restaurants/{id}", RESOURCE_ID)
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Updated Name"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /restaurants/{id} requires authentication")
    void deleteRestaurantReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/restaurants/{id}", RESOURCE_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /restaurants/{id} returns 204 for authenticated user")
    void deleteRestaurantReturns204WhenAuthenticated() throws Exception {
        // Use a dedicated restaurant for deletion so the shared RESOURCE_ID fixture stays intact for other tests.
        String response = mockMvc.perform(post("/restaurants")
                        .with(user("api-user").roles("USER"))
                        .contentType(JSON)
                        .content("""
                                {
                                  "name": "Restaurant to delete"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/restaurants/{id}", id)
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /restaurants/{id} returns 404 for unknown restaurant")
    void deleteRestaurantReturns404ForUnknownId() throws Exception {
        mockMvc.perform(delete("/restaurants/{id}", UUID.randomUUID())
                        .with(user("api-user").roles("USER")))
                .andExpect(status().isNotFound());
    }
}
