import java.io.*;
import java.net.*;
import java.util.*;

// This is a simple chat server using Java sockets
public class ChatSrv {
    // Stores all connected client sockets
    private static Set<Socket> clients = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket srv = new ServerSocket(8888)) {
            System.out.println("Server started on port 8888");

            // Keep accepting new client connections
            while (true) {
                Socket sock = srv.accept(); // Accept new client
                clients.add(sock);          // Add to list
                System.out.println("Client connected: " + sock);

                // Start thread to handle client messages
                new Thread(new ClientHandler(sock)).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    // This class handles each client in a separate thread
    static class ClientHandler implements Runnable {
        private Socket sock;
        private BufferedReader in;

        public ClientHandler(Socket sock) {
            this.sock = sock;
            try {
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                System.out.println("Error creating input stream");
            }
        }

        public void run() {
            try {
                String msg;
                // Keep reading messages from client
                while ((msg = in.readLine()) != null) {
                    sendToAll(msg); // Send message to all clients
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + sock);
            } finally {
                try {
                    sock.close();        // Close socket
                    clients.remove(sock); // Remove from list
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Send message to all connected clients
        private void sendToAll(String msg) throws IOException {
            for (Socket s : clients) {
                if (!s.isClosed()) {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println(msg); // Print message to client
                }
            }
        }
    }
}
