package de.quadflal.sdfccbackend.adapter.out.persistence.jpa;

import de.quadflal.sdfccbackend.adapter.out.persistence.model.UserPersistenceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserPersistenceModel, UUID> {

    Optional<UserPersistenceModel> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    Optional<UserPersistenceModel> findByEmailIgnoreCase(String email);
}
