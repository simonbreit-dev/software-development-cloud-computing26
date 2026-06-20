package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.adapter.out.persistence.jpa.UserRepository;
import de.quadflal.sdfccbackend.adapter.out.persistence.model.UserPersistenceModel;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.out.persistence.AuthPersistencePort;
import de.quadflal.sdfccbackend.port.out.persistence.UserPersistencePort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserAdapter implements AuthPersistencePort, UserPersistencePort {

    private final UserRepository userRepository;

    public UserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserPersistenceModel persistenceModel = new UserPersistenceModel();
        persistenceModel.setId(user.id());
        persistenceModel.setUsername(user.username());
        persistenceModel.setEmail(user.email());
        persistenceModel.setPasswordHash(user.passwordHash());
        persistenceModel.setCreatedAt(user.createdAt());
        return toDomain(userRepository.save(persistenceModel));
    }

    private User toDomain(UserPersistenceModel persistenceModel) {
        return new User(
                persistenceModel.getId(),
                persistenceModel.getUsername(),
                persistenceModel.getEmail(),
                persistenceModel.getPasswordHash(),
                persistenceModel.getCreatedAt()
        );
    }
}
