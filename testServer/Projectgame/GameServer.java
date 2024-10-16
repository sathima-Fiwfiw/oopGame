import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();

    private final int MAX_CANDIES = 10;
    private Point[] candyPositions = new Point[MAX_CANDIES];
    private Point[] ghostHandPositions = new Point[MAX_CANDIES];
    private double[] candySpeeds = new double[MAX_CANDIES];
    private Random random = new Random();
    private final int GAME_WIDTH = 1440;
    private final int GAME_HEIGHT = 810;

    private tradetime timer;

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

                playerName = in.readLine();
                characterCode = in.readLine();

                new Thread(GameServer.this::spawnAndMoveCandy).start();

                timer = new tradetime(0, 40);
                timer.startdown();

                new Thread(() -> {
                    while (timer.isend) {
                        try {
                            Thread.sleep(1000);
                            broadcastTime(timer.minutes, timer.seconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    broadcastTime(0, 0); // Notify all clients that time is up
                }).start();

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
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
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

        private void broadcastTime(int minutes, int seconds) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println("time," + minutes + "," + seconds);
                }
            }
        }
    }

    private void spawnAndMoveCandy() {
        for (int i = 0; i < MAX_CANDIES; i++) {
            int candyRandomX = random.nextInt(GAME_WIDTH - 100);
            candyPositions[i] = new Point(candyRandomX, 70);
            candySpeeds[i] = 5.0;

            int randomX = random.nextInt(GAME_WIDTH - 100);
            ghostHandPositions[i] = new Point(randomX, 710);
        }

        broadcastCandyAndGhostHands();

        while (true) {
            moveCandy();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveCandy() {
        for (int i = 0; i < MAX_CANDIES; i++) {
            candyPositions[i].y += candySpeeds[i];
            if (candyPositions[i].y > GAME_HEIGHT) {
                candyPositions[i].y = 70;
                candyPositions[i].x = random.nextInt(GAME_WIDTH - 100);
                candySpeeds[i] = 5.0;
            }
        }
        broadcastCandyAndGhostHands();
    }

    private void broadcastCandyAndGhostHands() {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                for (int i = 0; i < MAX_CANDIES; i++) {
                    writer.println("candy," + candyPositions[i].x + "," + candyPositions[i].y);
                    writer.println("ghostHand," + ghostHandPositions[i].x + "," + ghostHandPositions[i].y);
                }
            }
        }
    }
}

class tradetime {
    int minutes;
    int seconds;

    boolean isend = true;

    tradetime(int minutes, int seconds) {
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public void startdown() {
        new Thread(() -> {
            try {
                while (minutes > 0 || seconds > 0) {
                    Thread.sleep(1000);
                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                }
                isend = false; // Time has ended
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
