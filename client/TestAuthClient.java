package client;

import java.io.*;
import java.net.Socket;

public class TestAuthClient {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        out.println("LOGIN");
        out.println("alice");
        out.println("alice123");

        String response = in.readLine();

        if ("TOKEN".equals(response)) {
            String token = in.readLine();
            System.out.println("Received token:");
            System.out.println(token);
        } else {
            System.out.println("Login failed: " + in.readLine());
        }

        socket.close();
    }
}
