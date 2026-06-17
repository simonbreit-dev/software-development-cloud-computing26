package de.quadflal.sdfccbackend.core.model;

public record LoginCommand(
        String usernameOrEmail,
        String password
) {}
