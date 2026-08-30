package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import de.quadflal.sdfccbackend.port.in.web.ListsPort;
import de.quadflal.sdfccbackend.port.out.persistence.ListPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ListsService implements ListsPort {

    private final ListPersistencePort listPersistencePort;

    public ListsService(ListPersistencePort listPersistencePort) {
        this.listPersistencePort = listPersistencePort;
    }

    @Override
    public Page<RestaurantList> findAll(int page, int size) {
        return listPersistencePort.findAll(new de.quadflal.sdfccbackend.core.model.PageRequest(page, size, java.util.List.of()));
    }

    @Override
    public Optional<RestaurantList> findById(UUID id) {
        return listPersistencePort.findById(id);
    }

    @Override
    public RestaurantList create(RestaurantList restaurantList) {
        return listPersistencePort.save(restaurantList);
    }

    @Override
    public RestaurantList update(UUID id, RestaurantList restaurantList) {
        RestaurantList existing = listPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + id));

        RestaurantList updated = new RestaurantList(
                existing.id(),
                restaurantList.name(),
                restaurantList.description(),
                existing.ownerId(),
                existing.restaurantCount(),
                existing.createdAt(),
                java.time.OffsetDateTime.now()
        );

        return listPersistencePort.save(updated);
    }

    @Override
    public void deleteById(UUID id) {
        listPersistencePort.deleteById(id);
    }

    @Override
    public Page<Restaurant> findRestaurantsByListId(UUID listId, int page, int size) {
        return listPersistencePort.findRestaurantsByListId(listId, new de.quadflal.sdfccbackend.core.model.PageRequest(page, size, java.util.List.of()));
    }

    @Override
    public void addRestaurantToList(UUID listId, UUID restaurantId) {
        listPersistencePort.addRestaurantToList(listId, restaurantId);
    }

    @Override
    public void removeRestaurantFromList(UUID listId, UUID restaurantId) {
        listPersistencePort.removeRestaurantFromList(listId, restaurantId);
    }
}
