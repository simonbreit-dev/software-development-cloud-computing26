package de.quadflal.sdfccbackend.core.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RestaurantList(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        int restaurantCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
