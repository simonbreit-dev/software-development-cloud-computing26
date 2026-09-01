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

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * Loads or generates an RSA key pair used to sign and verify access tokens.
 * 
 * Loads from JWT_PRIVATE_KEY_PEM and JWT_PUBLIC_KEY_PEM environment variables if set.
 * Falls back to generating a fresh key pair for development/testing.
 * 
 * For production with multiple replicas, set the environment variables to a stable key
 * so tokens remain valid across restarts and replicas.
 */
@Configuration
public class JwtConfig {

    @Bean
    public KeyPair jwtKeyPair() throws Exception {
        String privateKeyPem = System.getenv("JWT_PRIVATE_KEY_PEM");
        String publicKeyPem = System.getenv("JWT_PUBLIC_KEY_PEM");
        
        if (privateKeyPem != null && publicKeyPem != null) {
            return loadKeyPairFromPem(privateKeyPem, publicKeyPem);
        }
        
        // Fallback: generate keypair for development/testing
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
    
    private KeyPair loadKeyPairFromPem(String privateKeyPem, String publicKeyPem) throws Exception {
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