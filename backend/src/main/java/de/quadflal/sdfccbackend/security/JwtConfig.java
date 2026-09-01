package de.quadflal.sdfccbackend.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * Loads or generates an RSA key pair used to sign and verify access tokens.
 * 
 * Loads from JWT_PRIVATE_KEY_PEM and JWT_PUBLIC_KEY_PEM environment variables, or
 * from the files named by JWT_PRIVATE_KEY_PATH and JWT_PUBLIC_KEY_PATH, if set.
 * Falls back to generating a fresh key pair only when
 * JWT_REQUIRE_CONFIGURED_KEYS is false, which is intended for development/testing.
 * 
 * For production with multiple replicas, configure stable key material through one
 * of these mechanisms so tokens remain valid across restarts and replicas.
 */
@Configuration
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair() throws GeneralSecurityException {
        String privateKeyPem = resolveKeyMaterial("JWT_PRIVATE_KEY_PEM", "JWT_PRIVATE_KEY_PATH");
        String publicKeyPem = resolveKeyMaterial("JWT_PUBLIC_KEY_PEM", "JWT_PUBLIC_KEY_PATH");
        boolean requireConfiguredKeys = Boolean.parseBoolean(
                System.getenv().getOrDefault("JWT_REQUIRE_CONFIGURED_KEYS", "false")
        );

        return createKeyPair(privateKeyPem, publicKeyPem, requireConfiguredKeys);
    }

    static String resolveKeyMaterial(String valueEnvironmentVariable, String pathEnvironmentVariable) {
        String value = System.getenv(valueEnvironmentVariable);
        String path = System.getenv(pathEnvironmentVariable);
        boolean hasValue = value != null && !value.isBlank();
        boolean hasPath = path != null && !path.isBlank();

        if (hasValue && hasPath) {
            throw new IllegalStateException(
                    valueEnvironmentVariable + " and " + pathEnvironmentVariable + " cannot both be configured"
            );
        }

        if (hasValue) {
            return value;
        }

        if (!hasPath) {
            return null;
        }

        return readKeyFile(path, pathEnvironmentVariable);
    }

    static String readKeyFile(String path, String pathEnvironmentVariable) {
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read JWT key file configured by " + pathEnvironmentVariable,
                    exception
            );
        }
    }

    static KeyPair createKeyPair(
            String privateKeyPem,
            String publicKeyPem,
            boolean requireConfiguredKeys
    ) throws GeneralSecurityException {
        boolean hasPrivateKey = privateKeyPem != null && !privateKeyPem.isBlank();
        boolean hasPublicKey = publicKeyPem != null && !publicKeyPem.isBlank();

        if (hasPrivateKey != hasPublicKey) {
            throw new IllegalStateException(
                    "JWT_PRIVATE_KEY_PEM and JWT_PUBLIC_KEY_PEM must either both be configured or both be absent"
            );
        }

        if (hasPrivateKey) {
            return loadKeyPairFromPem(privateKeyPem, publicKeyPem);
        }

        if (requireConfiguredKeys) {
            throw new IllegalStateException(
                    "JWT signing keys are required; configure JWT_PRIVATE_KEY_PEM and JWT_PUBLIC_KEY_PEM"
            );
        }

        // Ephemeral keys are safe only for single-process development and tests.
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static KeyPair loadKeyPairFromPem(
            String privateKeyPem,
            String publicKeyPem
    ) throws GeneralSecurityException {
        // Remove PEM headers and whitespace
        String privateKeyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
                new PKCS8EncodedKeySpec(privateKeyBytes)
        );
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(
                new X509EncodedKeySpec(publicKeyBytes)
        );

        if (!privateKey.getModulus().equals(publicKey.getModulus())) {
            throw new InvalidKeyException("JWT private and public keys do not form a matching RSA key pair");
        }

        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) jwtKeyPair.getPublic())
                .privateKey((RSAPrivateKey) jwtKeyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(KeyPair jwtKeyPair) {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtKeyPair.getPublic()).build();
    }
}
