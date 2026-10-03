package client;

import java.io.*;
import java.net.Socket;

public class TestFileClient {

    public static void main(String[] args) throws Exception {

        // Paste a VALID token here (from AuthServer test)
        String token = "YWxpY2V8VVNFUnwxNzY2ODQ3MDE2OTYxfDE3NjY4NDczMTY5NjF8UkFLTDFVRGFEZlRBRjl1NA==.m1a2ErKTBulJlflEW7TawyeP2JESuz3TxRoBQl4n8nIxJ1YRtq6l3TTeEtc95pbpg91zWJtQHO1yw82bo/zfOXfYip95KQI60XCs17NhCp/3KtHe56g//emvUe8uOO78UiVu7iTtgm2Xs81GU8t1HmUcJY1/gWoSWSATRRn8uzWj+5OxPbwf63Wqr1MwFEF9IolyO3ZBlZ0GY/5EvtmzKnEnxibSt5D0H7Ol6wDNXQunkoq7xWYG8/0m9YcThYu0dgl8PuoXl3GZExWdiMGn1kMCfptuf2T0FJJuyYRpoZ7Qi5aYVThk7gtwn1Y4BUN35TEfhZwIxEjQcmwoNdenyg==";

        Socket socket = new Socket("localhost", 6000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        out.println("REQUEST_FILE");
        out.println("secret.txt");
        out.println(token);

        String response = in.readLine();

        if ("OK".equals(response)) {
            System.out.println("File content:");
            System.out.println(in.readLine());
        } else {
            System.out.println("Access denied: " + in.readLine());
        }

        socket.close();
    }
}
