package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.core.model.Page;
import de.quadflal.sdfccbackend.core.model.PageRequest;
import de.quadflal.sdfccbackend.core.model.Restaurant;
import de.quadflal.sdfccbackend.core.model.RestaurantFilter;
import de.quadflal.sdfccbackend.port.out.persistence.RestaurantPersistencePort;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RestaurantPersistenceAdapter implements RestaurantPersistencePort {

    private static final Map<String, Comparator<RestaurantEntity>> SORTABLE_PROPERTIES = Map.of(
            "name", Comparator.comparing(RestaurantEntity::getName, Comparator.nullsLast(Comparator.naturalOrder())),
            "city", Comparator.comparing(RestaurantEntity::getCity, Comparator.nullsLast(Comparator.naturalOrder())),
            "country", Comparator.comparing(RestaurantEntity::getCountry, Comparator.nullsLast(Comparator.naturalOrder())),
            "createdAt", Comparator.comparing(RestaurantEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())),
            "updatedAt", Comparator.comparing(RestaurantEntity::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    private final RestaurantRepository restaurantRepository;

    public RestaurantPersistenceAdapter(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Page<Restaurant> findAll(PageRequest pageRequest, RestaurantFilter filter) {
        List<RestaurantEntity> matching = restaurantRepository.findAll().stream()
                .filter(entity -> filter == null || matchesFilter(entity, filter))
                .sorted(toComparator(pageRequest.sort()))
                .toList();

        List<Restaurant> content = matching.stream()
                .skip((long) pageRequest.page() * pageRequest.size())
                .limit(pageRequest.size())
                .map(this::toDomain)
                .toList();

        long total = matching.size();
        int totalPages = (int) Math.ceil((double) total / pageRequest.size());

        return new Page<>(content, pageRequest.page(), pageRequest.size(), total, totalPages);
    }

    private Comparator<RestaurantEntity> toComparator(List<String> sortCriteria) {
        List<SortCriteriaParser.SortCriterion> criteria = SortCriteriaParser.parse(sortCriteria);
        Comparator<RestaurantEntity> combined = null;
        for (SortCriteriaParser.SortCriterion criterion : criteria) {
            Comparator<RestaurantEntity> comparator = SORTABLE_PROPERTIES.get(criterion.property());
            if (comparator == null) {
                continue;
            }
            if (criterion.descending()) {
                comparator = comparator.reversed();
            }
            combined = combined == null ? comparator : combined.thenComparing(comparator);
        }
        return combined != null ? combined : (a, b) -> 0;
    }

    @Override
    public Optional<Restaurant> findById(UUID id) {
        return restaurantRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurantRepository.findById(restaurant.id())
                .orElse(new RestaurantEntity());

        entity.setId(restaurant.id() != null ? restaurant.id() : UUID.randomUUID());
        entity.setName(restaurant.name());
        entity.setDescription(restaurant.description());
        entity.setStreet(restaurant.street());
        entity.setCity(restaurant.city());
        entity.setPostalCode(restaurant.postalCode());
        entity.setCountry(restaurant.country());
        entity.setLatitude(restaurant.latitude());
        entity.setLongitude(restaurant.longitude());
        entity.setCreatedAt(restaurant.createdAt() != null ? restaurant.createdAt() : OffsetDateTime.now());
        entity.setUpdatedAt(restaurant.updatedAt() != null ? restaurant.updatedAt() : OffsetDateTime.now());

        return toDomain(restaurantRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        restaurantRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return restaurantRepository.existsById(id);
    }

    private boolean matchesFilter(RestaurantEntity entity, RestaurantFilter filter) {
        String search = filter.search() == null ? "" : filter.search().toLowerCase();
        if (!search.isEmpty()) {
            String haystack = (entity.getName() + " " + entity.getDescription()).toLowerCase();
            if (!haystack.contains(search)) {
                return false;
            }
        }
        if (filter.city() != null && !filter.city().equalsIgnoreCase(entity.getCity())) {
            return false;
        }
        if (filter.country() != null && !filter.country().equalsIgnoreCase(entity.getCountry())) {
            return false;
        }
        return true;
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
