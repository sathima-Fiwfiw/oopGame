import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    
    private List<Point> candyPositions = new ArrayList<>();
    private List<Point> ghostHandPositions = new ArrayList<>();
    private List<Double> candySpeeds = new ArrayList<>(); // Add candy speed array
    private Random random = new Random();
    private final int GAME_WIDTH = 1440;
    private final int GAME_HEIGHT = 810;

    public static void main(String[] args) {
        System.out.println("Game Server is running...");
        new GameServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String playerName;
        private String characterCode;
        private int x, y;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Receive player name and character code
                playerName = in.readLine();
                characterCode = in.readLine();

                // Start a separate thread for candy and ghost hands
                new Thread(GameServer.this::spawnAndMoveCandy).start();

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(",");
                    if (parts.length == 4) {
                        playerName = parts[0];
                        x = Integer.parseInt(parts[1]);
                        y = Integer.parseInt(parts[2]);
                        characterCode = parts[3];
                        broadcastPosition();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastPosition() {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(playerName + "," + x + "," + y + "," + characterCode);
                }
            }
        }
    }

    private void spawnAndMoveCandy() {
        while (true) {
            // Add new candies and ghost hands every few seconds
            Point candyPos = new Point(random.nextInt(GAME_WIDTH), 70);
            Point ghostHandPos = new Point(random.nextInt(GAME_WIDTH), 710);
            
            candyPositions.add(candyPos);
            ghostHandPositions.add(ghostHandPos);

            // Add random speed for the new candy
            candySpeeds.add(random.nextDouble() * 10.0 + 4.0);

            broadcastCandyAndGhostHands();
            
            moveCandy(); // Move the candies downward

            try {
                Thread.sleep(100); // Adjust as needed for smooth movement
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveCandy() {
        for (int i = 0; i < candyPositions.size(); i++) {
            Point candy = candyPositions.get(i);
            candy.y += candySpeeds.get(i); // Move candy down by its speed

            // If candy falls below the screen, reset its position
            if (candy.y > GAME_HEIGHT) {
                candy.y = 70; // Reset Y position
                candy.x = random.nextInt(GAME_WIDTH); // Reset X position
                candySpeeds.set(i, random.nextDouble() * 10.0 + 4.0); // Reset speed
            }
        }
        broadcastCandyAndGhostHands();
    }

    private void broadcastCandyAndGhostHands() {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                for (Point candyPos : candyPositions) {
                    writer.println("candy," + candyPos.x + "," + candyPos.y);
                }
                for (Point ghostHandPos : ghostHandPositions) {
                    writer.println("ghostHand," + ghostHandPos.x + "," + ghostHandPos.y);
                }
            }
        }
    }
}
