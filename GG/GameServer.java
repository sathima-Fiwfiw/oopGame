import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.awt.Point;


public class GameServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, Point> clientPositions = new HashMap<>(); // Store player positions by name

    public static void main(String[] args) {
        System.out.println("Game Server Started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Receive the player's name
                playerName = in.readLine();

                // Add the new player to the map with a default position
                synchronized (clientPositions) {
                    clientPositions.put(playerName, new Point(0, 0)); // Initialize with a default position
                }

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    // Parse the received position data
                    String[] parts = message.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);

                    // Update the player's position in the map
                    synchronized (clientPositions) {
                        clientPositions.put(playerName, new Point(x, y));
                    }

                    // Broadcast the updated position to all clients
                    broadcastPosition(playerName + "," + x + "," + y);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove the client from the map and list when they disconnect
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                synchronized (clientPositions) {
                    clientPositions.remove(playerName);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastPosition(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
