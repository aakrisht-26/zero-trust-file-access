package authserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Auth Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                handleClient(clientSocket);
            }

        } catch (IOException e) {
            System.err.println("Auth Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            String command = in.readLine();

            if (!"LOGIN".equals(command)) {
                out.println("ERROR");
                out.println("Invalid command");
                return;
            }

            String username = in.readLine();
            String password = in.readLine();

            boolean authenticated =
                    UserStore.authenticate(username, password);

            if (authenticated) {
                String role = UserStore.getRole(username);
                String token = TokenUtil.generateToken(username, role);

                out.println("TOKEN");
                out.println(token);

                System.out.println("Token issued for user: " + username);
            } else {
                out.println("ERROR");
                out.println("Invalid credentials");

                System.out.println("Failed login attempt for user: " + username);
            }

        } catch (Exception e) {
            System.err.println("Client handling error: " + e.getMessage());
        }
    }
}
