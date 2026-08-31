package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.in.web.RestaurantPort;
import de.quadflal.sdfccbackend.port.out.persistence.RestaurantPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantService implements RestaurantPort {

    private final RestaurantPersistencePort restaurantPersistencePort;

    public RestaurantService(RestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Page<Restaurant> findAll(int page, int size, RestaurantFilter filter, java.util.List<String> sort) {
        return restaurantPersistencePort.findAll(new de.quadflal.sdfccbackend.core.model.PageRequest(page, size, sort != null ? sort : java.util.List.of()), filter);
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return restaurantPersistencePort.findById(id);
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        return restaurantPersistencePort.save(restaurant);
    }

    @Override
    public Restaurant update(UUID id, Restaurant restaurant) {
        Restaurant existing = restaurantPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + id));

        Restaurant updated = new Restaurant(
                existing.id(),
                restaurant.name(),
                restaurant.description(),
                restaurant.street(),
                restaurant.city(),
                restaurant.postalCode(),
                restaurant.country(),
                restaurant.latitude(),
                restaurant.longitude(),
                existing.createdAt(),
                java.time.OffsetDateTime.now()
        );

        return restaurantPersistencePort.save(updated);
    }

    @Override
    public void deleteById(UUID id) {
        restaurantPersistencePort.deleteById(id);
    }
}
