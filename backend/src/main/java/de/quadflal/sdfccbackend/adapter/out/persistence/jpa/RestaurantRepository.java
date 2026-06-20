package de.quadflal.sdfccbackend.adapter.out.persistence.jpa;

import de.quadflal.sdfccbackend.adapter.out.persistence.model.RestaurantPersistenceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantPersistenceModel, UUID>, JpaSpecificationExecutor<RestaurantPersistenceModel> {

    @Query("""
            select r
            from RestaurantPersistenceModel r
            where exists (
                select 1
                from RestaurantListPersistenceModel lr
                where lr.listId = :listId
                  and lr.restaurantId = r.id
            )
            """)
    Page<RestaurantPersistenceModel> findByListId(@Param("listId") UUID listId, Pageable pageable);
}
