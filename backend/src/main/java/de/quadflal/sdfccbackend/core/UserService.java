package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import de.quadflal.sdfccbackend.port.out.persistence.UserPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserPort {

    private final UserPersistencePort userPersistencePort;

    public UserService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userPersistencePort.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPersistencePort.findByEmail(email);
    }

    @Override
    public User create(User user) {
        return userPersistencePort.save(user);
    }
}
