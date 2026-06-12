package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.RestaurantsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateRestaurantRequest;
import de.quadflal.sdfccbackend.port.in.web.RestaurantPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantsApi {

    private RestaurantPort restaurantPort;

    RestaurantController(RestaurantPort restaurantPort) {
        this.restaurantPort = restaurantPort;
    }

    @Override
    public ResponseEntity<RestaurantResponse> addRestaurant(AddRestaurantRequest addRestaurantRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantResponse> getRestaurantById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantPage> listRestaurants(Integer page, Integer size, @Nullable List<String> sort, @Nullable String search, @Nullable String city, @Nullable String country) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantResponse> updateRestaurant(UUID id, UpdateRestaurantRequest updateRestaurantRequest) {
        return null;
    }
}
