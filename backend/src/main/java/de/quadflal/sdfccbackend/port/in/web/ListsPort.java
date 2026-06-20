package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import java.util.UUID;

public interface ListsPort {

    Page<RestaurantList> listLists(PageRequest pageRequest);

    RestaurantList getListById(UUID id);

    RestaurantList createList(RestaurantList list);

    RestaurantList updateList(UUID id, RestaurantList patch);

    void deleteList(UUID id);

    Page<Restaurant> getRestaurantsInList(UUID listId, PageRequest pageRequest);

    void addRestaurantToList(UUID listId, UUID restaurantId);

    void removeRestaurantFromList(UUID listId, UUID restaurantId);
}
