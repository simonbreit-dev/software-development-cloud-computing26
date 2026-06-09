package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.ListsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.*;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ListsController implements ListsApi {
    @Override
    public ResponseEntity<Void> addRestaurantToList(UUID id, AddRestaurantToListRequest addRestaurantToListRequest) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantList> createList(CreateListRequest createListRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteListById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantList> getListById(UUID id) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantPage> getRestaurantsInList(UUID id, Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantListPage> listLists(Integer page, Integer size, @Nullable List<String> sort) {
        return null;
    }

    @Override
    public ResponseEntity<Void> removeRestaurantFromList(UUID id, UUID restaurantId) {
        return null;
    }

    @Override
    public ResponseEntity<RestaurantList> updateList(UUID id, UpdateListRequest updateListRequest) {
        return null;
    }
}
