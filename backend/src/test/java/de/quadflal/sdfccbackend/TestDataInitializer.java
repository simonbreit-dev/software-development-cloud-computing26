package de.quadflal.sdfccbackend;

import de.quadflal.sdfccbackend.adapter.out.persistence.UserEntity;
import de.quadflal.sdfccbackend.adapter.out.persistence.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@Profile("test")
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            UserEntity user = new UserEntity(
                    UUID.randomUUID(),
                    "demo-user",
                    "user@example.com",
                    passwordEncoder.encode("password123"),
                    OffsetDateTime.now()
            );
            userRepository.save(user);
        }
    }
}
