package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.RestaurantsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateRestaurantRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.in.web.RestaurantPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class RestaurantController implements RestaurantsApi {

    private final RestaurantPort restaurantPort;

    RestaurantController(RestaurantPort restaurantPort) {
        this.restaurantPort = restaurantPort;
    }

    @Override
    public ResponseEntity<RestaurantResponse> addRestaurant(AddRestaurantRequest addRestaurantRequest) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(),
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
        );
        Restaurant created = restaurantPort.create(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(convert(created));
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantById(UUID id) {
        if (restaurantPort.findById(id).isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        restaurantPort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantResponse> getRestaurantById(UUID id) {
        Restaurant restaurant = restaurantPort.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(convert(restaurant));
    }

    @Override
    public ResponseEntity<RestaurantPage> listRestaurants(Integer page, Integer size, @Nullable List<String> sort, @Nullable String search, @Nullable String city, @Nullable String country) {
        RestaurantFilter filter = new RestaurantFilter(search, city, country);
        de.quadflal.sdfccbackend.core.model.Page<Restaurant> result = restaurantPort.findAll(
                page != null ? page : 0,
                size != null ? size : 20,
                filter
        );
        RestaurantPage pageResponse = new RestaurantPage();
        pageResponse.setContent(result.content().stream().map(this::convert).toList());
        pageResponse.setPage(new PageMetadata(result.page(), result.size(), result.totalElements(), result.totalPages()));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<RestaurantResponse> updateRestaurant(UUID id, UpdateRestaurantRequest updateRestaurantRequest) {
        Restaurant current = restaurantPort.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));

        Restaurant updated = new Restaurant(
                current.id(),
                updateRestaurantRequest.getName() != null ? updateRestaurantRequest.getName() : current.name(),
                updateRestaurantRequest.getDescription() != null ? updateRestaurantRequest.getDescription() : current.description(),
                updateRestaurantRequest.getStreet() != null ? updateRestaurantRequest.getStreet() : current.street(),
                updateRestaurantRequest.getCity() != null ? updateRestaurantRequest.getCity() : current.city(),
                updateRestaurantRequest.getPostalCode() != null ? updateRestaurantRequest.getPostalCode() : current.postalCode(),
                updateRestaurantRequest.getCountry() != null ? updateRestaurantRequest.getCountry() : current.country(),
                updateRestaurantRequest.getLatitude() != null ? updateRestaurantRequest.getLatitude() : current.latitude(),
                updateRestaurantRequest.getLongitude() != null ? updateRestaurantRequest.getLongitude() : current.longitude(),
                current.createdAt(),
                OffsetDateTime.now()
        );

        Restaurant persisted = restaurantPort.update(id, updated);
        return ResponseEntity.ok(convert(persisted));
    }

    private RestaurantResponse convert(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.id());
        response.setName(restaurant.name());
        response.setDescription(restaurant.description());
        response.setStreet(restaurant.street());
        response.setCity(restaurant.city());
        response.setPostalCode(restaurant.postalCode());
        response.setCountry(restaurant.country());
        response.setLatitude(restaurant.latitude());
        response.setLongitude(restaurant.longitude());
        response.setCreatedAt(restaurant.createdAt());
        response.setUpdatedAt(restaurant.updatedAt());
        return response;
    }
}
