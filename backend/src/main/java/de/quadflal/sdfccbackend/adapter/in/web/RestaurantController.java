package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.RestaurantsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateRestaurantRequest;
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
        RestaurantResponse response = new RestaurantResponse();
        response.setId(UUID.randomUUID());
        response.setName(addRestaurantRequest.getName() != null ? addRestaurantRequest.getName() : "");
        response.setCity(addRestaurantRequest.getCity());
        response.setCountry(addRestaurantRequest.getCountry());
        response.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteRestaurantById(UUID id) {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantResponse> getRestaurantById(UUID id) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(id);
        response.setName("Sample Restaurant");
        response.setCity("Berlin");
        response.setCountry("Germany");
        response.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<RestaurantPage> listRestaurants(Integer page, Integer size, @Nullable List<String> sort, @Nullable String search, @Nullable String city, @Nullable String country) {
        RestaurantPage pageResponse = new RestaurantPage();
        pageResponse.setContent(List.of());
        pageResponse.setPage(new PageMetadata(page != null ? page : 0, size != null ? size : 20, 0L, 0));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<RestaurantResponse> updateRestaurant(UUID id, UpdateRestaurantRequest updateRestaurantRequest) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(id);
        response.setName(updateRestaurantRequest.getName() != null ? updateRestaurantRequest.getName() : "Updated Restaurant");
        response.setCity(updateRestaurantRequest.getCity());
        response.setCountry(updateRestaurantRequest.getCountry());
        response.setCreatedAt(OffsetDateTime.now());
        response.setUpdatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }
}
