package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.exception.UserNotFoundException;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.UserPort;
import de.quadflal.sdfccbackend.port.out.persistence.UserPersistencePort;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserPort {

    private final UserPersistencePort userPersistencePort;

    public UserService(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public User getCurrentUser(String usernameOrEmail) {
        return userPersistencePort.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(UserNotFoundException::new);
    }
}
