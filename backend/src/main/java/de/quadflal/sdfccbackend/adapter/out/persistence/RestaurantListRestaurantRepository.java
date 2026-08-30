package de.quadflal.sdfccbackend.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantListRestaurantRepository extends JpaRepository<RestaurantListRestaurantEntity, UUID> {
    boolean existsByRestaurantListIdAndRestaurantId(UUID listId, UUID restaurantId);
    Optional<RestaurantListRestaurantEntity> findByRestaurantListIdAndRestaurantId(UUID listId, UUID restaurantId);
    Page<RestaurantListRestaurantEntity> findByRestaurantListId(UUID listId, Pageable pageable);
    long countByRestaurantListId(UUID listId);
}
