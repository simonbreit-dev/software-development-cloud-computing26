package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User create(User user);
}
