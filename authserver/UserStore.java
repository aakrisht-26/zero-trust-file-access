package authserver;

import shared.CryptoUtils;
import java.util.HashMap;
import java.util.Map;

public class UserStore {

    private static class User {
        String passwordHash;
        byte[] salt;
        String role;

        User(String username, String passwordHash, byte[] salt, String role) {
            this.passwordHash = passwordHash;
            this.salt = salt;
            this.role = role;
        }
    }

    private static final Map<String, User> users = new HashMap<>();

    // Initialize users (hardcoded for project)
    static {
        addUser("alice", "alice123", "USER");
        addUser("bob", "bob123", "ADMIN");
    }

    private static void addUser(String username, String password, String role) {
        byte[] salt = CryptoUtils.generateSalt();
        String hash = CryptoUtils.hashPassword(password.toCharArray(), salt);
        users.put(username, new User(username, hash, salt, role));
    }

    // Verify login credentials
    public static boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }

        String attemptedHash =
                CryptoUtils.hashPassword(password.toCharArray(), user.salt);

        return attemptedHash.equals(user.passwordHash);
    }

    // Get role of authenticated user
    public static String getRole(String username) {
        User user = users.get(username);
        return (user != null) ? user.role : null;
    }
}
