package de.quadflal.sdfccbackend.core.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Restaurant(
        UUID id,
        String name,
        String description,
        String street,
        String city,
        String postalCode,
        String country,
        Double latitude,
        Double longitude,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
