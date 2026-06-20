package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.exception.RestaurantNotFoundException;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.in.web.RestaurantPort;
import de.quadflal.sdfccbackend.port.out.persistence.RestaurantPersistencePort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class RestaurantService implements RestaurantPort {

    private final RestaurantPersistencePort restaurantPersistencePort;

    public RestaurantService(RestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Page<Restaurant> listRestaurants(PageRequest pageRequest, RestaurantFilter filter) {
        return restaurantPersistencePort.findAll(pageRequest, filter);
    }

    @Override
    public Restaurant getRestaurantById(UUID id) {
        return restaurantPersistencePort.findById(id)
                .orElseThrow(RestaurantNotFoundException::new);
    }

    @Override
    public Restaurant addRestaurant(Restaurant restaurant) {
        OffsetDateTime now = OffsetDateTime.now();
        Restaurant restaurantToSave = new Restaurant(
                restaurant.id(),
                restaurant.name(),
                restaurant.description(),
                restaurant.street(),
                restaurant.city(),
                restaurant.postalCode(),
                restaurant.country(),
                restaurant.latitude(),
                restaurant.longitude(),
                restaurant.createdAt() != null ? restaurant.createdAt() : now,
                restaurant.updatedAt()
        );
        return restaurantPersistencePort.save(restaurantToSave);
    }

    @Override
    public Restaurant updateRestaurant(UUID id, Restaurant patch) {
        Restaurant current = getRestaurantById(id);
        Restaurant updated = new Restaurant(
                current.id(),
                patch.name() != null ? patch.name() : current.name(),
                patch.description() != null ? patch.description() : current.description(),
                patch.street() != null ? patch.street() : current.street(),
                patch.city() != null ? patch.city() : current.city(),
                patch.postalCode() != null ? patch.postalCode() : current.postalCode(),
                patch.country() != null ? patch.country() : current.country(),
                patch.latitude() != null ? patch.latitude() : current.latitude(),
                patch.longitude() != null ? patch.longitude() : current.longitude(),
                current.createdAt(),
                OffsetDateTime.now()
        );
        return restaurantPersistencePort.save(updated);
    }

    @Override
    public void deleteRestaurant(UUID id) {
        if (!restaurantPersistencePort.existsById(id)) {
            throw new RestaurantNotFoundException();
        }
        restaurantPersistencePort.deleteById(id);
    }
}
