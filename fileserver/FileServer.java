package fileserver;

import authserver.TokenUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileServer {

    private static final int PORT = 6000;
    private static final String FILE_DIR = "files/";

    public static void main(String[] args) {

        System.out.println("File Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("File request received");

                handleClient(clientSocket);
            }

        } catch (IOException e) {
            System.err.println("File Server error: " + e.getMessage());
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

            if (!"REQUEST_FILE".equals(command)) {
                out.println("DENIED");
                out.println("Invalid command");
                return;
            }

            String filename = in.readLine();
            String token = in.readLine();

            // ZERO TRUST CHECK
            if (!TokenUtil.verifyToken(token)) {
                out.println("DENIED");
                out.println("Invalid or expired token");
                return;
            }

            Path filePath = Paths.get(FILE_DIR + filename);

            if (!Files.exists(filePath)) {
                out.println("DENIED");
                out.println("File not found");
                return;
            }

            // Access allowed
            out.println("OK");

            byte[] fileBytes = Files.readAllBytes(filePath);
            String fileContent = new String(fileBytes);

            out.println(fileContent);

            System.out.println("File sent: " + filename);

        } catch (Exception e) {
            System.err.println("File request error: " + e.getMessage());
        }
    }
}
