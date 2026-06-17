package de.quadflal.sdfccbackend.core.model;

public record LoginResult(
        String accessToken,
        int expiresIn
) {}
