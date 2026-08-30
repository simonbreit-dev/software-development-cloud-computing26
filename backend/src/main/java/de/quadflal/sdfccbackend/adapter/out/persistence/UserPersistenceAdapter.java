package de.quadflal.sdfccbackend.adapter.out.persistence;

import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.out.persistence.UserPersistencePort;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userRepository.findById(user.id())
                .orElse(new UserEntity());

        entity.setId(user.id() != null ? user.id() : UUID.randomUUID());
        entity.setUsername(user.username());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setCreatedAt(user.createdAt() != null ? user.createdAt() : OffsetDateTime.now());

        return toDomain(userRepository.save(entity));
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt()
        );
    }
}
