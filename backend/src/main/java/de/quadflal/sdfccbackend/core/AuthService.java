package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import de.quadflal.sdfccbackend.port.out.persistence.AuthPersistencePort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements AuthPort {

    private final AuthPersistencePort authPersistencePort;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthPersistencePort authPersistencePort, PasswordEncoder passwordEncoder) {
        this.authPersistencePort = authPersistencePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> login(String usernameOrEmail, String rawPassword) {
        if (usernameOrEmail == null || rawPassword == null) {
            return Optional.empty();
        }

        return authPersistencePort.findByUsernameOrEmail(usernameOrEmail)
                .filter(user -> passwordEncoder.matches(rawPassword, user.passwordHash()));
    }
}
