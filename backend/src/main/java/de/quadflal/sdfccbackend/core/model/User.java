package de.quadflal.sdfccbackend.core.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        String passwordHash,
        OffsetDateTime createdAt
) {}
