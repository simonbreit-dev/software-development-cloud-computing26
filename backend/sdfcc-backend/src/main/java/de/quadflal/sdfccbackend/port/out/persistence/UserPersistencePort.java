package de.quadflal.sdfccbackend.port.out.persistence;

import de.quadflal.sdfccbackend.core.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserPersistencePort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    User save(User user);
}
