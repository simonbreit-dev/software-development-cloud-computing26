package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.RestaurantsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateRestaurantRequest;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.in.web.RestaurantPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantsApi {

    private final RestaurantPort restaurantPort;

    public RestaurantController(RestaurantPort restaurantPort) {
        this.restaurantPort = restaurantPort;
    }

    @Override
    public ResponseEntity<RestaurantResponse> addRestaurant(AddRestaurantRequest addRestaurantRequest) {
        Restaurant created = restaurantPort.addRestaurant(new Restaurant(
                null,
                addRestaurantRequest.getName(),
                addRestaurantRequest.getDescription(),
                addRestaurantRequest.getStreet(),
                addRestaurantRequest.getCity(),
                addRestaurantRequest.getPostalCode(),
                addRestaurantRequest.getCountry(),
                addRestaurantRequest.getLatitude(),
                addRestaurantRequest.getLongitude(),
                OffsetDateTime.now(),
                null
        ));
        RestaurantResponse response = toResponse(created);
        return ResponseEntity.created(URI.create("/restaurants/" + response.getId())).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantById(UUID id) {
        restaurantPort.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantResponse> getRestaurantById(UUID id) {
        return ResponseEntity.ok(toResponse(restaurantPort.getRestaurantById(id)));
    }

    @Override
    public ResponseEntity<RestaurantPage> listRestaurants(Integer page, Integer size, @Nullable List<String> sort, @Nullable String search, @Nullable String city, @Nullable String country) {
        PageRequest pageRequest = new PageRequest(page, size, sort == null ? List.of() : sort);
        RestaurantFilter filter = new RestaurantFilter(search, city, country);
        Page<Restaurant> restaurants = restaurantPort.listRestaurants(pageRequest, filter);
        List<RestaurantResponse> content = restaurants.content().stream().map(this::toResponse).toList();
        PageMetadata metadata = new PageMetadata(restaurants.page(), restaurants.size(), restaurants.totalElements(), restaurants.totalPages());
        return ResponseEntity.ok(new RestaurantPage(content, metadata));
    }

    @Override
    public ResponseEntity<RestaurantResponse> updateRestaurant(UUID id, UpdateRestaurantRequest updateRestaurantRequest) {
        Restaurant patch = new Restaurant(
                id,
                updateRestaurantRequest.getName(),
                updateRestaurantRequest.getDescription(),
                updateRestaurantRequest.getStreet(),
                updateRestaurantRequest.getCity(),
                updateRestaurantRequest.getPostalCode(),
                updateRestaurantRequest.getCountry(),
                updateRestaurantRequest.getLatitude(),
                updateRestaurantRequest.getLongitude(),
                null,
                null
        );
        return ResponseEntity.ok(toResponse(restaurantPort.updateRestaurant(id, patch)));
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
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
