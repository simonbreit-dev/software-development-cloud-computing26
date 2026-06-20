package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.exception.ListNotFoundException;
import de.quadflal.sdfccbackend.core.exception.RestaurantAlreadyInListException;
import de.quadflal.sdfccbackend.core.exception.RestaurantNotFoundException;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import de.quadflal.sdfccbackend.port.in.web.ListsPort;
import de.quadflal.sdfccbackend.port.out.persistence.ListPersistencePort;
import de.quadflal.sdfccbackend.port.out.persistence.RestaurantPersistencePort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ListsService implements ListsPort {

    private final ListPersistencePort listPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;

    public ListsService(ListPersistencePort listPersistencePort, RestaurantPersistencePort restaurantPersistencePort) {
        this.listPersistencePort = listPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Page<RestaurantList> listLists(PageRequest pageRequest) {
        return listPersistencePort.findAll(pageRequest);
    }

    @Override
    public RestaurantList getListById(UUID id) {
        return listPersistencePort.findById(id)
                .orElseThrow(ListNotFoundException::new);
    }

    @Override
    public RestaurantList createList(RestaurantList list) {
        OffsetDateTime now = OffsetDateTime.now();
        RestaurantList listToCreate = new RestaurantList(
                list.id(),
                list.name(),
                list.description(),
                list.ownerId(),
                0,
                list.createdAt() != null ? list.createdAt() : now,
                null
        );
        return listPersistencePort.save(listToCreate);
    }

    @Override
    public RestaurantList updateList(UUID id, RestaurantList patch) {
        RestaurantList current = getListById(id);
        RestaurantList updated = new RestaurantList(
                current.id(),
                patch.name() != null ? patch.name() : current.name(),
                patch.description() != null ? patch.description() : current.description(),
                current.ownerId(),
                current.restaurantCount(),
                current.createdAt(),
                OffsetDateTime.now()
        );
        return listPersistencePort.save(updated);
    }

    @Override
    public void deleteList(UUID id) {
        if (!listPersistencePort.existsById(id)) {
            throw new ListNotFoundException();
        }
        listPersistencePort.deleteById(id);
    }

    @Override
    public Page<Restaurant> getRestaurantsInList(UUID listId, PageRequest pageRequest) {
        if (!listPersistencePort.existsById(listId)) {
            throw new ListNotFoundException();
        }
        return listPersistencePort.findRestaurantsByListId(listId, pageRequest);
    }

    @Override
    public void addRestaurantToList(UUID listId, UUID restaurantId) {
        if (!listPersistencePort.existsById(listId)) {
            throw new ListNotFoundException();
        }
        if (!restaurantPersistencePort.existsById(restaurantId)) {
            throw new RestaurantNotFoundException();
        }
        if (listPersistencePort.restaurantExistsInList(listId, restaurantId)) {
            throw new RestaurantAlreadyInListException();
        }
        listPersistencePort.addRestaurantToList(listId, restaurantId);
    }

    @Override
    public void removeRestaurantFromList(UUID listId, UUID restaurantId) {
        if (!listPersistencePort.existsById(listId)) {
            throw new ListNotFoundException();
        }
        if (!listPersistencePort.restaurantExistsInList(listId, restaurantId)) {
            throw new RestaurantNotFoundException();
        }
        listPersistencePort.removeRestaurantFromList(listId, restaurantId);
    }
}
