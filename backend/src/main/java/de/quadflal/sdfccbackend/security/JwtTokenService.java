package de.quadflal.sdfccbackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {

    private static final String ISSUER = "sdfcc-backend";

    private final JwtEncoder jwtEncoder;
    private final long expirySeconds;

    public JwtTokenService(JwtEncoder jwtEncoder, @Value("${jwt.expiration-seconds:3600}") long expirySeconds) {
        this.jwtEncoder = jwtEncoder;
        this.expirySeconds = expirySeconds;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirySeconds))
                .subject(username)
                .claim("roles", "USER")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getExpirySeconds() {
        return expirySeconds;
    }
}
