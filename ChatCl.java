import java.io.*;
import java.net.*;

// Simple chat client using socket
public class ChatCl {
    public static void main(String[] args) {
        try (
            Socket sock = new Socket("localhost", 8888); // Connect to server
            BufferedReader con = new BufferedReader(new InputStreamReader(System.in)); // Read from keyboard
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // Read from server
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true) // Send to server
        ) {
            System.out.println("Connected to server. Start typing:");

            // Thread to receive messages from server
            new Thread(() -> {
                try {
                    String msgFromSrv;
                    while ((msgFromSrv = in.readLine()) != null) {
                        System.out.println("Server: " + msgFromSrv);
                    }
                } catch (IOException e) {
                    System.out.println("Server closed connection.");
                }
            }).start();

            // Main thread sends user messages
            String msg;
            while ((msg = con.readLine()) != null) {
                out.println(msg); // Send message
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
