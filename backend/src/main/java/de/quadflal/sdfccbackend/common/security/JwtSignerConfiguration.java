package de.quadflal.sdfccbackend.common.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtSignerConfiguration {

    @Bean
    public SecretKey jwtSigningKey(@Value("${jwt.signing-key}") String signingKeyBase64) {
        byte[] key;
        try {
            key = Base64.getDecoder().decode(signingKeyBase64.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid JWT signing key: expected Base64", exception);
        }

        if (key.length < 48) {
            throw new IllegalStateException("Invalid JWT signing key: expected at least 384 bits");
        }

        return new SecretKeySpec(key, "HmacSHA384");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSigningKey) {
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSigningKey)
                .algorithm(JWSAlgorithm.HS384)
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSigningKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSigningKey)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }
}