package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantPort {
    Page<Restaurant> findAll(int page, int size, RestaurantFilter filter, java.util.List<String> sort);
    Optional<Restaurant> findById(UUID id);
    Restaurant create(Restaurant restaurant);
    Restaurant update(UUID id, Restaurant restaurant);
    void deleteById(UUID id);
}
