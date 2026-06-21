package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.ListsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantToListRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.CreateListRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantListPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateListRequest;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.port.in.web.ListsPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ListsController implements ListsApi {

    private final ListsPort listsPort;
    private final RestaurantMapper restaurantMapper;

    public ListsController(ListsPort listsPort, RestaurantMapper restaurantMapper) {
        this.listsPort = listsPort;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public ResponseEntity<Void> addRestaurantToList(UUID id, AddRestaurantToListRequest addRestaurantToListRequest) {
        listsPort.addRestaurantToList(id, addRestaurantToListRequest.getRestaurantId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> createList(CreateListRequest createListRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID ownerId = UUID.nameUUIDFromBytes(authentication.getName().getBytes(StandardCharsets.UTF_8));
        de.quadflal.sdfccbackend.core.model.RestaurantList created = listsPort.createList(new de.quadflal.sdfccbackend.core.model.RestaurantList(
                null,
                createListRequest.getName(),
                createListRequest.getDescription(),
                ownerId,
                0,
                OffsetDateTime.now(),
                null
        ));
        RestaurantList response = toResponse(created);
        return ResponseEntity.created(URI.create("/lists/" + response.getId())).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteListById(UUID id) {
        listsPort.deleteList(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> getListById(UUID id) {
        return ResponseEntity.ok(toResponse(listsPort.getListById(id)));
    }

    @Override
    public ResponseEntity<RestaurantPage> getRestaurantsInList(UUID id, Integer page, Integer size) {
        PageRequest pageRequest = new PageRequest(page, size, List.of());
        Page<de.quadflal.sdfccbackend.core.model.Restaurant> restaurants = listsPort.getRestaurantsInList(id, pageRequest);
        List<RestaurantResponse> content = restaurants.content().stream().map(restaurantMapper::toResponse).toList();
        PageMetadata metadata = new PageMetadata(restaurants.page(), restaurants.size(), restaurants.totalElements(), restaurants.totalPages());
        return ResponseEntity.ok(new RestaurantPage(content, metadata));
    }

    @Override
    public ResponseEntity<RestaurantListPage> listLists(Integer page, Integer size, @Nullable List<String> sort) {
        PageRequest pageRequest = new PageRequest(page, size, sort == null ? List.of() : sort);
        Page<de.quadflal.sdfccbackend.core.model.RestaurantList> lists = listsPort.listLists(pageRequest);
        List<RestaurantList> content = lists.content().stream().map(this::toResponse).toList();
        PageMetadata metadata = new PageMetadata(lists.page(), lists.size(), lists.totalElements(), lists.totalPages());
        return ResponseEntity.ok(new RestaurantListPage(content, metadata));
    }

    @Override
    public ResponseEntity<Void> removeRestaurantFromList(UUID id, UUID restaurantId) {
        listsPort.removeRestaurantFromList(id, restaurantId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RestaurantList> updateList(UUID id, UpdateListRequest updateListRequest) {
        de.quadflal.sdfccbackend.core.model.RestaurantList patch = new de.quadflal.sdfccbackend.core.model.RestaurantList(
                id,
                updateListRequest.getName(),
                updateListRequest.getDescription(),
                null,
                0,
                null,
                null
        );
        return ResponseEntity.ok(toResponse(listsPort.updateList(id, patch)));
    }

    private RestaurantList toResponse(de.quadflal.sdfccbackend.core.model.RestaurantList list) {
        RestaurantList response = new RestaurantList(
                list.id(),
                list.name(),
                list.ownerId(),
                list.restaurantCount(),
                list.createdAt()
        );
        response.setDescription(list.description());
        response.setUpdatedAt(list.updatedAt());
        return response;
    }
}
