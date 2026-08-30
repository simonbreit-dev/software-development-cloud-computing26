package de.quadflal.sdfccbackend;

import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantEntity;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Profile("test")
public class RestaurantTestDataInitializer {

    private final RestaurantRepository restaurantRepository;

    public RestaurantTestDataInitializer(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @PostConstruct
    public void init() {
        // Create test restaurants with the UUIDs used in tests
        seedRestaurant(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"), "Test Restaurant");
        seedRestaurant(UUID.fromString("4fa85f64-5717-4562-b3fc-2c963f66afa6"), "Second Test Restaurant");
    }

    private void seedRestaurant(UUID id, String name) {
        if (restaurantRepository.findById(id).isEmpty()) {
            RestaurantEntity restaurant = new RestaurantEntity(
                    id,
                    name,
                    "A test restaurant for contract testing",
                    "123 Test Street",
                    "Berlin",
                    "10115",
                    "Germany",
                    52.52,
                    13.40,
                    OffsetDateTime.now(),
                    null
            );
            restaurantRepository.save(restaurant);
        }
    }
}
