import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<PrintWriter, PlayerInfo> clientPositions = new HashMap<>(); // Store client positions and names

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

    private static class PlayerInfo {
        String name;
        int x, y;

        public PlayerInfo(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }
                
                // Receive player name
                String playerName = reader.readLine();
                PlayerInfo playerInfo = new PlayerInfo(playerName, new Random().nextInt(380), new Random().nextInt(380)); // Set initial random position
                synchronized (clientPositions) {
                    clientPositions.put(out, playerInfo);
                }

                // Notify other clients about the new player
                notifyAllClients();

                String message;
                while ((message = reader.readLine()) != null) {
                    // Update player position
                    String[] parts = message.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    
                    synchronized (clientPositions) {
                        playerInfo.x = x;
                        playerInfo.y = y;
                    }
                    // Notify all clients with the updated positions
                    notifyAllClients();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                synchronized (clientPositions) {
                    clientPositions.remove(out);
                }
            }
        }

        private void notifyAllClients() {
            for (PrintWriter writer : clientWriters) {
                synchronized (clientPositions) {
                    for (PlayerInfo info : clientPositions.values()) {
                        writer.println(info.name + "," + info.x + "," + info.y); // Send name, x, and y
                    }
                }
            }
        }
    }
}
