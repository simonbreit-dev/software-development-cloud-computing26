package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import de.quadflal.sdfccbackend.port.out.persistence.ListPersistencePort;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ListPersistenceAdapter implements ListPersistencePort {

    private final RestaurantListRepository restaurantListRepository;
    private final RestaurantListRestaurantRepository restaurantListRestaurantRepository;
    private final RestaurantRepository restaurantRepository;

    public ListPersistenceAdapter(RestaurantListRepository restaurantListRepository,
                                 RestaurantListRestaurantRepository restaurantListRestaurantRepository,
                                 RestaurantRepository restaurantRepository) {
        this.restaurantListRepository = restaurantListRepository;
        this.restaurantListRestaurantRepository = restaurantListRestaurantRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Page<RestaurantList> findAll(PageRequest pageRequest) {
        List<RestaurantList> content = restaurantListRepository.findAll().stream()
                .skip((long) pageRequest.page() * pageRequest.size())
                .limit(pageRequest.size())
                .map(this::toDomain)
                .toList();

        long total = restaurantListRepository.count();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / pageRequest.size());

        return new Page<>(content, pageRequest.page(), pageRequest.size(), total, totalPages);
    }

    @Override
    public Optional<RestaurantList> findById(UUID id) {
        return restaurantListRepository.findById(id).map(this::toDomain);
    }

    @Override
    public RestaurantList save(RestaurantList restaurantList) {
        RestaurantListEntity entity = restaurantListRepository.findById(restaurantList.id())
                .orElse(new RestaurantListEntity());

        entity.setId(restaurantList.id() != null ? restaurantList.id() : UUID.randomUUID());
        entity.setName(restaurantList.name());
        entity.setDescription(restaurantList.description());
        entity.setOwnerId(restaurantList.ownerId());
        entity.setRestaurantCount(restaurantList.restaurantCount());
        entity.setCreatedAt(restaurantList.createdAt() != null ? restaurantList.createdAt() : OffsetDateTime.now());
        entity.setUpdatedAt(restaurantList.updatedAt() != null ? restaurantList.updatedAt() : OffsetDateTime.now());

        return toDomain(restaurantListRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        restaurantListRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return restaurantListRepository.existsById(id);
    }

    @Override
    public Page<Restaurant> findRestaurantsByListId(UUID listId, PageRequest pageRequest) {
        var page = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size());
        var rows = restaurantListRestaurantRepository.findByRestaurantListId(listId, page);
        List<Restaurant> content = rows.getContent().stream()
                .map(row -> toDomain(row.getRestaurant()))
                .toList();

        return new Page<>(content, pageRequest.page(), pageRequest.size(), rows.getTotalElements(), rows.getTotalPages());
    }

    @Override
    public void addRestaurantToList(UUID listId, UUID restaurantId) {
        RestaurantListEntity list = restaurantListRepository.findById(listId).orElse(null);
        if (list == null) {
            return;
        }

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant == null) {
            return;
        }

        if (restaurantListRestaurantRepository.existsByRestaurantListIdAndRestaurantId(listId, restaurantId)) {
            return;
        }

        RestaurantListRestaurantEntity row = new RestaurantListRestaurantEntity(
                UUID.randomUUID(),
                list,
                restaurant,
                OffsetDateTime.now()
        );
        restaurantListRestaurantRepository.save(row);

        list.setRestaurantCount((int) restaurantListRestaurantRepository.countByRestaurantListId(listId));
        list.setUpdatedAt(OffsetDateTime.now());
        restaurantListRepository.save(list);
    }

    @Override
    public void removeRestaurantFromList(UUID listId, UUID restaurantId) {
        RestaurantListRestaurantEntity row = restaurantListRestaurantRepository.findByRestaurantListIdAndRestaurantId(listId, restaurantId)
                .orElse(null);
        if (row == null) {
            return;
        }

        restaurantListRestaurantRepository.delete(row);

        RestaurantListEntity list = restaurantListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("List not found: " + listId));
        list.setRestaurantCount((int) restaurantListRestaurantRepository.countByRestaurantListId(listId));
        list.setUpdatedAt(OffsetDateTime.now());
        restaurantListRepository.save(list);
    }

    @Override
    public boolean restaurantExistsInList(UUID listId, UUID restaurantId) {
        return restaurantListRestaurantRepository.existsByRestaurantListIdAndRestaurantId(listId, restaurantId);
    }

    private RestaurantList toDomain(RestaurantListEntity entity) {
        return new RestaurantList(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getRestaurantCount() == null ? 0 : entity.getRestaurantCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private Restaurant toDomain(RestaurantEntity entity) {
        return new Restaurant(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStreet(),
                entity.getCity(),
                entity.getPostalCode(),
                entity.getCountry(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
