package authserver;

import shared.CryptoUtils;
import shared.KeyManager;

import java.util.Base64;

public class TokenUtil {

    // Token validity: 5 minutes
    private static final long TOKEN_VALIDITY_MS = 5 * 60 * 1000;

    // Create a signed token
    public static String generateToken(String username, String role) {

    long issuedTime = System.currentTimeMillis();
    long expiryTime = issuedTime + TOKEN_VALIDITY_MS;
    String nonce = CryptoUtils.generateNonce();

    String tokenData = username + "|" +
                       role + "|" +
                       issuedTime + "|" +
                       expiryTime + "|" +
                       nonce;

    // SIGN (already Base64)
    String signature = CryptoUtils.signData(
            tokenData,
            KeyManager.getPrivateKey()
    );

    // Encode ONLY tokenData
    String encodedData =
            Base64.getEncoder().encodeToString(tokenData.getBytes());

    // DO NOT encode signature again
    return encodedData + "." + signature;
}


    // Verify token signature and expiry
    public static boolean verifyToken(String token) {

    try {
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return false;
        }

        String tokenData =
                new String(Base64.getDecoder().decode(parts[0]));
        String signature = parts[1]; // already Base64

        boolean validSignature =
                CryptoUtils.verifySignature(
                        tokenData,
                        signature,
                        KeyManager.getPublicKey()
                );

        if (!validSignature) {
            return false;
        }

        String[] fields = tokenData.split("\\|");
        long expiryTime = Long.parseLong(fields[3]);

        return System.currentTimeMillis() <= expiryTime;

    } catch (Exception e) {
        return false;
    }
}


    // Extract username (after verification)
    public static String getUsername(String token) {
        String data = extractTokenData(token);
        return (data != null) ? data.split("\\|")[0] : null;
    }

    // Extract role (after verification)
    public static String getRole(String token) {
        String data = extractTokenData(token);
        return (data != null) ? data.split("\\|")[1] : null;
    }

    // Internal helper
    private static String extractTokenData(String token) {
        try {
            String[] parts = token.split("\\.");
            return new String(Base64.getDecoder().decode(parts[0]));
        } catch (Exception e) {
            return null;
        }
    }
}
