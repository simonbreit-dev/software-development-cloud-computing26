package de.quadflal.sdfccbackend.core;

import de.quadflal.sdfccbackend.core.model.LoginCommand;
import de.quadflal.sdfccbackend.core.model.LoginResult;
import de.quadflal.sdfccbackend.core.model.User;
import de.quadflal.sdfccbackend.port.in.web.AuthPort;
import de.quadflal.sdfccbackend.port.out.persistence.AuthPersistencePort;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService implements AuthPort {

    private final JwtEncoder jwtEncoder;
    private final AuthPersistencePort authPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AuthService(JwtEncoder jwtEncoder, AuthPersistencePort authPersistencePort, PasswordEncoder passwordEncoder, Environment environment) {
        this.jwtEncoder = jwtEncoder;
        this.authPersistencePort = authPersistencePort;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }


    @Override
    public LoginResult login(LoginCommand command) {
        Optional<User> userOpt = authPersistencePort.findByUsernameOrEmail(command.usernameOrEmail());

        if (userOpt.isEmpty()) {
            // TODO: add correct exception handling
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            // TODO: add correct exception handling
            throw new RuntimeException("Invalid password");
        }

        Instant instantNow = Instant.now();
        int expirationHours = environment.getProperty("jwt.expiration-hours", Integer.class, 1);
        Duration ttl = Duration.ofHours(expirationHours);
        Instant expiresAt = instantNow.plus(ttl);

        String scope = "USER";

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(environment.getProperty("jwt.issuer", "sdfcc-backend"))
                .issuedAt(instantNow)
                .expiresAt(expiresAt)
                .subject(user.username())
                .claim("scope", scope)
                .build();

        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        int timeToLive = Math.toIntExact(ttl.getSeconds());
        return new LoginResult(tokenValue, timeToLive);
    }

}
