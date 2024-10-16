import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.util.List;

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
        new GameServer().startServer(); // เรียกเมธอดที่เปิดเซิร์ฟเวอร์
    }

    public void startServer() { // เปิดเซิร์ฟเวอร์และรอให้คลienten เชื่อมต่อ
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) { // วนลูปจนกว่าจะมีเครื่องมาเชื่อมต่อ
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread { 
        private Socket socket; // การเชื่อมต่อกับผู้เล่น
        private PrintWriter out; // สำหรับส่งข้อมูลไปยังผู้เล่น
        private String playerName; // ชื่อของผู้เล่น
        private String characterCode; // โค้ดตัวละครของผู้เล่น
        private int x, y; // ตำแหน่งของผู้เล่นในเกม

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
            // Add new candies
            Point candyPos = new Point(100, 70);
            candyPositions.add(candyPos);

            // Randomly position ghost hand on the X-axis
            int randomX = random.nextInt(GAME_WIDTH - 100); // Random X position within game width
            Point ghostHandPos = new Point(randomX, 710);
            ghostHandPositions.add(ghostHandPos);

            // Add random speed for the new candy
            candySpeeds.add(1.0);

            broadcastCandyAndGhostHands();
            moveCandy(); // Move the candies downward

            try {
                Thread.sleep(20); // Adjust as needed for smooth movement
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
                candy.x = 100; // Reset X position
                candySpeeds.set(i, 1.0); // Reset speed
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
