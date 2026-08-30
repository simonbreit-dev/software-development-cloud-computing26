package de.quadflal.sdfccbackend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantListRepository extends JpaRepository<RestaurantListEntity, UUID> {
}
