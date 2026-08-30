package de.quadflal.sdfccbackend.port.in.web;

import de.quadflal.sdfccbackend.core.model.User;
import java.util.Optional;

public interface AuthPort {
    Optional<User> login(String usernameOrEmail, String rawPassword);
}
