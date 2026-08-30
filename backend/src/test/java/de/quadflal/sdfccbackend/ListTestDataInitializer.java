package de.quadflal.sdfccbackend;

import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantListEntity;
import de.quadflal.sdfccbackend.adapter.out.persistence.RestaurantListRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Profile("test")
public class ListTestDataInitializer {

    private final RestaurantListRepository restaurantListRepository;

    public ListTestDataInitializer(RestaurantListRepository restaurantListRepository) {
        this.restaurantListRepository = restaurantListRepository;
    }

    @PostConstruct
    public void init() {
        // Create test list with the UUID used in tests
        UUID testId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        if (restaurantListRepository.findById(testId).isEmpty()) {
            RestaurantListEntity list = new RestaurantListEntity(
                    testId,
                    "Test List",
                    "A test list for contract testing",
                    UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                    0,
                    OffsetDateTime.now(),
                    null
            );
            restaurantListRepository.save(list);
        }
    }
}
