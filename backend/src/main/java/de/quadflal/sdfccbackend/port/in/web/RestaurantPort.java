package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import java.util.UUID;

public interface RestaurantPort {

    Page<Restaurant> listRestaurants(PageRequest pageRequest, RestaurantFilter filter);

    Restaurant getRestaurantById(UUID id);

    Restaurant addRestaurant(Restaurant restaurant);

    Restaurant updateRestaurant(UUID id, Restaurant patch);

    void deleteRestaurant(UUID id);
}
