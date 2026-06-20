package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.adapter.out.persistence.jpa.ListRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.jpa.RestaurantListRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.jpa.RestaurantRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.model.ListPersistenceModel;
import de.quadflal.sdfccbackend.adapter.out.persistence.model.RestaurantListPersistenceModel;
import de.quadflal.sdfccbackend.adapter.out.persistence.model.RestaurantPersistenceModel;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantList;
import de.quadflal.sdfccbackend.port.out.persistence.ListPersistencePort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListAdapter implements ListPersistencePort {

    private final ListRepository listRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantListRepository restaurantListRepository;

    public ListAdapter(
            ListRepository listRepository,
            RestaurantRepository restaurantRepository,
            RestaurantListRepository restaurantListRepository
    ) {
        this.listRepository = listRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantListRepository = restaurantListRepository;
    }

    @Override
    public Page<RestaurantList> findAll(PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        org.springframework.data.domain.Page<ListPersistenceModel> persistencePage = listRepository.findAll(pageable);
        List<RestaurantList> content = persistencePage.getContent().stream().map(this::toDomain).toList();
        return new Page<>(
                content,
                persistencePage.getNumber(),
                persistencePage.getSize(),
                persistencePage.getTotalElements(),
                persistencePage.getTotalPages()
        );
    }

    @Override
    public Optional<RestaurantList> findById(UUID id) {
        return listRepository.findById(id).map(this::toDomain);
    }

    @Override
    public RestaurantList save(RestaurantList restaurantList) {
        ListPersistenceModel persistenceModel = toPersistence(restaurantList);
        ListPersistenceModel savedModel = listRepository.save(persistenceModel);
        return toDomain(savedModel);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        restaurantListRepository.deleteByListId(id);
        listRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return listRepository.existsById(id);
    }

    @Override
    public Page<Restaurant> findRestaurantsByListId(UUID listId, PageRequest pageRequest) {
        Pageable pageable = toPageable(pageRequest);
        org.springframework.data.domain.Page<RestaurantPersistenceModel> persistencePage = restaurantRepository.findByListId(listId, pageable);
        List<Restaurant> content = persistencePage.getContent().stream().map(this::toDomain).toList();
        return new Page<>(
                content,
                persistencePage.getNumber(),
                persistencePage.getSize(),
                persistencePage.getTotalElements(),
                persistencePage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void addRestaurantToList(UUID listId, UUID restaurantId) {
        if (restaurantListRepository.existsByListIdAndRestaurantId(listId, restaurantId)) {
            return;
        }

        RestaurantListPersistenceModel relation = new RestaurantListPersistenceModel();
        relation.setListId(listId);
        relation.setRestaurantId(restaurantId);
        restaurantListRepository.save(relation);
        updateListRestaurantCount(listId);
    }

    @Override
    @Transactional
    public void removeRestaurantFromList(UUID listId, UUID restaurantId) {
        restaurantListRepository.deleteByListIdAndRestaurantId(listId, restaurantId);
        updateListRestaurantCount(listId);
    }

    @Override
    public boolean restaurantExistsInList(UUID listId, UUID restaurantId) {
        return restaurantListRepository.existsByListIdAndRestaurantId(listId, restaurantId);
    }

    private RestaurantList toDomain(ListPersistenceModel persistenceModel) {
        return new RestaurantList(
                persistenceModel.getId(),
                persistenceModel.getName(),
                persistenceModel.getDescription(),
                persistenceModel.getOwnerId(),
                persistenceModel.getRestaurantCount(),
                persistenceModel.getCreatedAt(),
                persistenceModel.getUpdatedAt()
        );
    }

    private ListPersistenceModel toPersistence(RestaurantList list) {
        ListPersistenceModel persistenceModel = new ListPersistenceModel();
        persistenceModel.setId(list.id());
        persistenceModel.setName(list.name());
        persistenceModel.setDescription(list.description());
        persistenceModel.setOwnerId(list.ownerId());
        persistenceModel.setRestaurantCount(list.restaurantCount());
        persistenceModel.setCreatedAt(list.createdAt());
        persistenceModel.setUpdatedAt(list.updatedAt());
        return persistenceModel;
    }

    private Restaurant toDomain(RestaurantPersistenceModel persistenceModel) {
        return new Restaurant(
                persistenceModel.getId(),
                persistenceModel.getName(),
                persistenceModel.getDescription(),
                persistenceModel.getStreet(),
                persistenceModel.getCity(),
                persistenceModel.getPostalCode(),
                persistenceModel.getCountry(),
                persistenceModel.getLatitude(),
                persistenceModel.getLongitude(),
                persistenceModel.getCreatedAt(),
                persistenceModel.getUpdatedAt()
        );
    }

    private Pageable toPageable(PageRequest pageRequest) {
        int page = pageRequest != null ? pageRequest.page() : 0;
        int size = pageRequest != null ? pageRequest.size() : 20;
        List<String> sortValues = pageRequest != null ? pageRequest.sort() : List.of();
        return org.springframework.data.domain.PageRequest.of(page, size, toSort(sortValues));
    }

    private Sort toSort(List<String> sortValues) {
        if (sortValues == null || sortValues.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortValue : sortValues) {
            if (sortValue == null || sortValue.trim().isEmpty()) {
                continue;
            }
            String[] parts = sortValue.split(",", 2);
            String property = parts[0].trim();
            if (property.isEmpty()) {
                continue;
            }
            String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";
            Sort.Direction sortDirection = "desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(sortDirection, property));
        }

        if (orders.isEmpty()) {
            return Sort.by(Sort.Order.desc("createdAt"));
        }
        return Sort.by(orders);
    }

    private void updateListRestaurantCount(UUID listId) {
        listRepository.findById(listId).ifPresent(list -> {
            int count = (int) restaurantListRepository.countByListId(listId);
            list.setRestaurantCount(count);
            listRepository.save(list);
        });
    }
}
