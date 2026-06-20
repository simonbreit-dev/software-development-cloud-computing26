package de.quadflal.sdfccbackend.adapter.out.persistence.jpa;

import de.quadflal.sdfccbackend.adapter.out.persistence.model.RestaurantListPersistenceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantListRepository extends JpaRepository<RestaurantListPersistenceModel, UUID> {

    Page<RestaurantListPersistenceModel> findByListId(UUID listId, Pageable pageable);

    boolean existsByListIdAndRestaurantId(UUID listId, UUID restaurantId);

    void deleteByListIdAndRestaurantId(UUID listId, UUID restaurantId);

    void deleteByListId(UUID listId);

    long countByListId(UUID listId);
}
