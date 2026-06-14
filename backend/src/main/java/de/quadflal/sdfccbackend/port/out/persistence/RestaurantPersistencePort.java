package de.quadflal.sdfccbackend.port.out.persistence;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantPersistencePort {

    Page<Restaurant> findAll(PageRequest pageRequest, RestaurantFilter filter);

    Optional<Restaurant> findById(UUID id);

    Restaurant save(Restaurant restaurant);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
