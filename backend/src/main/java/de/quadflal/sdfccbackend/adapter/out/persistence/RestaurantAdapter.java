package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.adapter.out.persistence.jpa.RestaurantRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.model.RestaurantPersistenceModel;
import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.out.persistence.RestaurantPersistencePort;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantAdapter implements RestaurantPersistencePort {

    private final RestaurantRepository restaurantRepository;

    public RestaurantAdapter(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Page<Restaurant> findAll(PageRequest pageRequest, RestaurantFilter filter) {
        Pageable pageable = toPageable(pageRequest);
        Specification<RestaurantPersistenceModel> specification = buildSpecification(filter);
        org.springframework.data.domain.Page<RestaurantPersistenceModel> persistencePage = restaurantRepository.findAll(specification, pageable);
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
    public Optional<Restaurant> findById(UUID id) {
        return restaurantRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantPersistenceModel persistenceModel = toPersistence(restaurant);
        return toDomain(restaurantRepository.save(persistenceModel));
    }

    @Override
    public void deleteById(UUID id) {
        restaurantRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return restaurantRepository.existsById(id);
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

    private RestaurantPersistenceModel toPersistence(Restaurant restaurant) {
        RestaurantPersistenceModel persistenceModel = new RestaurantPersistenceModel();
        persistenceModel.setId(restaurant.id());
        persistenceModel.setName(restaurant.name());
        persistenceModel.setDescription(restaurant.description());
        persistenceModel.setStreet(restaurant.street());
        persistenceModel.setCity(restaurant.city());
        persistenceModel.setPostalCode(restaurant.postalCode());
        persistenceModel.setCountry(restaurant.country());
        persistenceModel.setLatitude(restaurant.latitude());
        persistenceModel.setLongitude(restaurant.longitude());
        persistenceModel.setCreatedAt(restaurant.createdAt());
        persistenceModel.setUpdatedAt(restaurant.updatedAt());
        return persistenceModel;
    }

    private Specification<RestaurantPersistenceModel> buildSpecification(RestaurantFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (hasText(filter.search())) {
                    String searchValue = "%" + filter.search().trim().toLowerCase() + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchValue),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchValue)
                    ));
                }
                if (hasText(filter.city())) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("city")),
                            filter.city().trim().toLowerCase()
                    ));
                }
                if (hasText(filter.country())) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("country")),
                            filter.country().trim().toLowerCase()
                    ));
                }
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
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
            if (!hasText(sortValue)) {
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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
