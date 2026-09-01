package de.quadflal.sdfccbackend.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtConfigTest {

    @Test
    void generatesEphemeralKeyPairWhenBlankKeysAreAllowed() throws Exception {
        KeyPair keyPair = JwtConfig.createKeyPair("", "  ", false);

        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
    }

    @Test
    void rejectsBlankKeysWhenConfiguredKeysAreRequired() {
        assertThrows(
                IllegalStateException.class,
                () -> JwtConfig.createKeyPair("", "", true)
        );
    }

    @Test
    void rejectsPartialKeyConfiguration() {
        assertThrows(
                IllegalStateException.class,
                () -> JwtConfig.createKeyPair("private-key", "", false)
        );
    }

    @Test
    void readsKeyMaterialFromFile(@TempDir Path temporaryDirectory) throws Exception {
        Path keyFile = temporaryDirectory.resolve("jwt.pem");
        Files.writeString(keyFile, "key-material");

        assertEquals("key-material", JwtConfig.readKeyFile(keyFile.toString(), "JWT_TEST_KEY_PATH"));
    }

    @Test
    void loadsMatchingPemKeyPair() throws Exception {
        KeyPair source = generateKeyPair();

        KeyPair loaded = JwtConfig.createKeyPair(
                toPem("PRIVATE KEY", source.getPrivate().getEncoded()),
                toPem("PUBLIC KEY", source.getPublic().getEncoded()),
                true
        );

        assertArrayEquals(source.getPrivate().getEncoded(), loaded.getPrivate().getEncoded());
        assertArrayEquals(source.getPublic().getEncoded(), loaded.getPublic().getEncoded());
    }

    @Test
    void rejectsMismatchedPemKeyPair() throws Exception {
        KeyPair first = generateKeyPair();
        KeyPair second = generateKeyPair();

        assertThrows(
                java.security.InvalidKeyException.class,
                () -> JwtConfig.createKeyPair(
                        toPem("PRIVATE KEY", first.getPrivate().getEncoded()),
                        toPem("PUBLIC KEY", second.getPublic().getEncoded()),
                        true
                )
        );
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    private static String toPem(String type, byte[] encoded) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded)
                + "\n-----END " + type + "-----";
    }
}
