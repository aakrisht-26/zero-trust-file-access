package shared;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyManager {

    private static final String PRIVATE_KEY_FILE = "keys/private.key";
    private static final String PUBLIC_KEY_FILE = "keys/public.key";

    static {
        try {
            File privateKey = new File(PRIVATE_KEY_FILE);
            File publicKey = new File(PUBLIC_KEY_FILE);

            if (!privateKey.exists() || !publicKey.exists()) {
                generateAndSaveKeys();
            }
        } catch (Exception e) {
            throw new RuntimeException("Key initialization failed");
        }
    }

    private static void generateAndSaveKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        saveKey(PRIVATE_KEY_FILE, keyPair.getPrivate().getEncoded());
        saveKey(PUBLIC_KEY_FILE, keyPair.getPublic().getEncoded());
    }

    private static void saveKey(String fileName, byte[] key) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(Base64.getEncoder().encode(key));
        fos.close();
    }

    private static byte[] loadKey(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] data = new byte[1024];
        int bytesRead;

        while ((bytesRead = fis.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        fis.close();
        return Base64.getDecoder().decode(buffer.toByteArray());
    }

    public static PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = loadKey(PRIVATE_KEY_FILE);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key");
        }
    }

    public static PublicKey getPublicKey() {
        try {
            byte[] keyBytes = loadKey(PUBLIC_KEY_FILE);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key");
        }
    }
}
