package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.AddRestaurantToListResult;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;

import java.util.Optional;
import java.util.UUID;

public interface ListsPort {
    Page<RestaurantList> findAll(int page, int size, java.util.List<String> sort);
    Optional<RestaurantList> findById(UUID id);
    RestaurantList create(RestaurantList restaurantList);
    RestaurantList update(UUID id, RestaurantList restaurantList);
    void deleteById(UUID id);
    Page<Restaurant> findRestaurantsByListId(UUID listId, int page, int size);
    AddRestaurantToListResult addRestaurantToList(UUID listId, UUID restaurantId);
    boolean removeRestaurantFromList(UUID listId, UUID restaurantId);
}
