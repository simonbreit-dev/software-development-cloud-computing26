package de.quadflal.sdfccbackend.adapter.in.web;

import de.quadflal.sdfccbackend.adapter.in.web.generated.api.ListsApi;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.AddRestaurantToListRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.CreateListRequest;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.PageMetadata;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantListPage;
import de.quadflal.sdfccbackend.adapter.in.web.generated.model.UpdateListRequest;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.port.in.web.ListsPort;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ListsController implements ListsApi {

    private final ListsPort listsPort;
    private final UserPort userPort;

    ListsController(ListsPort listsPort, UserPort userPort) {
        this.listsPort = listsPort;
        this.userPort = userPort;
    }

    @Override
    public ResponseEntity<Void> addRestaurantToList(UUID id, AddRestaurantToListRequest addRestaurantToListRequest) {
        de.quadflal.sdfccbackend.core.model.AddRestaurantToListResult result =
                listsPort.addRestaurantToList(id, addRestaurantToListRequest.getRestaurantId());
        switch (result) {
            case NOT_FOUND -> throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
            case ALREADY_EXISTS -> throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT, "Restaurant is already in this list");
            case ADDED -> { }
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList> createList(CreateListRequest createListRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID ownerId = userPort.findByUsername(username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR))
                .id();

        de.quadflal.sdfccbackend.core.model.RestaurantList domainList = new de.quadflal.sdfccbackend.core.model.RestaurantList(
                UUID.randomUUID(),
                createListRequest.getName(),
                createListRequest.getDescription(),
                ownerId,
                0,
                OffsetDateTime.now(),
                null
        );
        de.quadflal.sdfccbackend.core.model.RestaurantList created = listsPort.create(domainList);
        return ResponseEntity.status(HttpStatus.CREATED).body(convert(created));
    }

    @Override
    public ResponseEntity<Void> deleteListById(UUID id) {
        if (listsPort.findById(id).isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        listsPort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList> getListById(UUID id) {
        de.quadflal.sdfccbackend.core.model.RestaurantList list = listsPort.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(convert(list));
    }

    @Override
    public ResponseEntity<RestaurantPage> getRestaurantsInList(UUID id, Integer page, Integer size) {
        Page<Restaurant> result = listsPort.findRestaurantsByListId(id, page != null ? page : 0, size != null ? size : 20);
        RestaurantPage pageResponse = new RestaurantPage();
        pageResponse.setContent(result.content().stream().map(this::convert).toList());
        pageResponse.setPage(new PageMetadata(result.page(), result.size(), result.totalElements(), result.totalPages()));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<RestaurantListPage> listLists(Integer page, Integer size, @Nullable List<String> sort) {
        Page<de.quadflal.sdfccbackend.core.model.RestaurantList> result = listsPort.findAll(page != null ? page : 0, size != null ? size : 20, sort);
        RestaurantListPage pageResponse = new RestaurantListPage();
        pageResponse.setContent(result.content().stream().map(this::convert).toList());
        pageResponse.setPage(new PageMetadata(result.page(), result.size(), result.totalElements(), result.totalPages()));
        return ResponseEntity.ok(pageResponse);
    }

    @Override
    public ResponseEntity<Void> removeRestaurantFromList(UUID id, UUID restaurantId) {
        boolean removed = listsPort.removeRestaurantFromList(id, restaurantId);
        if (!removed) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList> updateList(UUID id, UpdateListRequest updateListRequest) {
        de.quadflal.sdfccbackend.core.model.RestaurantList current = listsPort.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));

        de.quadflal.sdfccbackend.core.model.RestaurantList updated = new de.quadflal.sdfccbackend.core.model.RestaurantList(
                current.id(),
                updateListRequest.getName() != null ? updateListRequest.getName() : current.name(),
                updateListRequest.getDescription() != null ? updateListRequest.getDescription() : current.description(),
                current.ownerId(),
                current.restaurantCount(),
                current.createdAt(),
                OffsetDateTime.now()
        );

        de.quadflal.sdfccbackend.core.model.RestaurantList persisted = listsPort.update(id, updated);

        return ResponseEntity.ok(convert(persisted));
    }

    private de.quadflal.sdfccbackend.core.model.RestaurantList convert(de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList list) {
        return new de.quadflal.sdfccbackend.core.model.RestaurantList(
                list.getId(),
                list.getName(),
                list.getDescription(),
                list.getOwnerId(),
                list.getRestaurantCount() != null ? list.getRestaurantCount() : 0,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    private de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList convert(de.quadflal.sdfccbackend.core.model.RestaurantList list) {
        de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList response = new de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantList();
        response.setId(list.id());
        response.setName(list.name());
        response.setDescription(list.description());
        response.setOwnerId(list.ownerId());
        response.setRestaurantCount(list.restaurantCount());
        response.setCreatedAt(list.createdAt());
        response.setUpdatedAt(list.updatedAt());
        return response;
    }

    private de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse convert(Restaurant restaurant) {
        de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse response = new de.quadflal.sdfccbackend.adapter.in.web.generated.model.RestaurantResponse();
        response.setId(restaurant.id());
        response.setName(restaurant.name());
        response.setDescription(restaurant.description());
        response.setStreet(restaurant.street());
        response.setCity(restaurant.city());
        response.setPostalCode(restaurant.postalCode());
        response.setCountry(restaurant.country());
        response.setLatitude(restaurant.latitude());
        response.setLongitude(restaurant.longitude());
        response.setCreatedAt(restaurant.createdAt());
        response.setUpdatedAt(restaurant.updatedAt());
        return response;
    }
}
