package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String AUTH_HOST = "localhost";
    private static final int AUTH_PORT = 5000;

    private static final String FILE_HOST = "localhost";
    private static final int FILE_PORT = 6000;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            // -------- LOGIN --------
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            String token = authenticate(username, password);

            if (token == null) {
                System.out.println("Login failed. Exiting.");
                return;
            }

            System.out.println("\nLogin successful.");
            System.out.println("Access token received.\n");
            System.out.println("TOKEN (for demo):");
            System.out.println(token);
            // -------- FILE ACCESS --------
            System.out.print("Enter file name to access: ");
            String filename = scanner.nextLine();

            requestFile(filename, token);

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String authenticate(String username, String password) throws Exception {

        Socket socket = new Socket(AUTH_HOST, AUTH_PORT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        out.println("LOGIN");
        out.println(username);
        out.println(password);

        String response = in.readLine();

        if ("TOKEN".equals(response)) {
            String token = in.readLine();
            socket.close();
            return token;
        } else {
            System.out.println("Auth Server response: " + in.readLine());
            socket.close();
            return null;
        }
    }

    private static void requestFile(String filename, String token) throws Exception {

        Socket socket = new Socket(FILE_HOST, FILE_PORT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        out.println("REQUEST_FILE");
        out.println(filename);
        out.println(token);

        String response = in.readLine();

        if ("OK".equals(response)) {
            System.out.println("\nFile content:\n");
            System.out.println(in.readLine());
        } else {
            System.out.println("\nAccess denied: " + in.readLine());
        }

        socket.close();
    }
}
