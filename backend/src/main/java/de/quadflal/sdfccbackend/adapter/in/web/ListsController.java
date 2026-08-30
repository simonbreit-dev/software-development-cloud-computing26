package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.ListsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.*;
import de.quadflal.sdfccbackend.port.in.web.ListsPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ListsController implements ListsApi {

    private final ListsPort listsPort;

    ListsController(ListsPort listsPort) {
        this.listsPort = listsPort;
    }

    @Override
    public ResponseEntity<Void> addRestaurantToList(UUID id, AddRestaurantToListRequest addRestaurantToListRequest) {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> createList(CreateListRequest createListRequest) {
        RestaurantList list = new RestaurantList();
        list.setId(UUID.randomUUID());
        list.setName(createListRequest.getName());
        list.setDescription(createListRequest.getDescription());
        list.setOwnerId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
        list.setRestaurantCount(0);
        list.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
    }

    @Override
    public ResponseEntity<Void> deleteListById(UUID id) {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> getListById(UUID id) {
        RestaurantList list = new RestaurantList();
        list.setId(id);
        list.setName("Sample List");
        list.setOwnerId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
        list.setRestaurantCount(0);
        list.setCreatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<RestaurantPage> getRestaurantsInList(UUID id, Integer page, Integer size) {
        RestaurantPage pageResponse = new RestaurantPage();
        pageResponse.setContent(List.of());
        pageResponse.setPage(new PageMetadata(page != null ? page : 0, size != null ? size : 20, 0L, 0));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<RestaurantListPage> listLists(Integer page, Integer size, @Nullable List<String> sort) {
        RestaurantListPage pageResponse = new RestaurantListPage();
        pageResponse.setContent(List.of());
        pageResponse.setPage(new PageMetadata(page != null ? page : 0, size != null ? size : 20, 0L, 0));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<Void> removeRestaurantFromList(UUID id, UUID restaurantId) {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> updateList(UUID id, UpdateListRequest updateListRequest) {
        RestaurantList list = new RestaurantList();
        list.setId(id);
        list.setName(updateListRequest.getName() != null ? updateListRequest.getName() : "Updated List");
        list.setDescription(updateListRequest.getDescription());
        list.setOwnerId(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"));
        list.setRestaurantCount(0);
        list.setCreatedAt(OffsetDateTime.now());
        list.setUpdatedAt(OffsetDateTime.now());
        return ResponseEntity.ok(list);
    }
}
