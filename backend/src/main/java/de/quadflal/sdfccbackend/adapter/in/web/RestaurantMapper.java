package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public RestaurantResponse toResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse(restaurant.id(), restaurant.name(), restaurant.createdAt());
        response.setDescription(restaurant.description());
        response.setStreet(restaurant.street());
        response.setCity(restaurant.city());
        response.setPostalCode(restaurant.postalCode());
        response.setCountry(restaurant.country());
        response.setLatitude(restaurant.latitude());
        response.setLongitude(restaurant.longitude());
        response.setUpdatedAt(restaurant.updatedAt());
        return response;
    }
}
