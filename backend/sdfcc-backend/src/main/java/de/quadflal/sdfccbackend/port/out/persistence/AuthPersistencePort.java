package de.quadflal.sdfccbackend.port.out.persistence;

import de.quadflal.sdfccbackend.core.model.User;
import java.util.Optional;

public interface AuthPersistencePort {

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}
