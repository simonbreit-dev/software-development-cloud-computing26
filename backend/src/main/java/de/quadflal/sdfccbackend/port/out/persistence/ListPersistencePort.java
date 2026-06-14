package de.quadflal.sdfccbackend.port.out.persistence;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import java.util.Optional;
import java.util.UUID;

public interface ListPersistencePort {

    Page<RestaurantList> findAll(PageRequest pageRequest);

    Optional<RestaurantList> findById(UUID id);

    RestaurantList save(RestaurantList restaurantList);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Page<Restaurant> findRestaurantsByListId(UUID listId, PageRequest pageRequest);

    void addRestaurantToList(UUID listId, UUID restaurantId);

    void removeRestaurantFromList(UUID listId, UUID restaurantId);

    boolean restaurantExistsInList(UUID listId, UUID restaurantId);
}
