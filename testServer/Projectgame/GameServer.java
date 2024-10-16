import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.util.List;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    
    private final int MAX_CANDIES = 10; // จำนวนสูงสุดของลูกอม
    private Point[] candyPositions = new Point[MAX_CANDIES];
    private Point[] ghostHandPositions = new Point[MAX_CANDIES];
    private double[] candySpeeds = new double[MAX_CANDIES]; // ใช้เป็นอาเรย์สำหรับความเร็วลูกอม
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

                // รับชื่อผู้เล่นและโค้ดตัวละคร
                playerName = in.readLine();
                characterCode = in.readLine();

                // เริ่ม thread แยกสำหรับลูกอมและมือผี
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
        for (int i = 0; i < MAX_CANDIES; i++) {
            // กำหนดค่าเริ่มต้นให้กับตำแหน่งและความเร็วของลูกอม
            int candyRandomX = random.nextInt(GAME_WIDTH - 100);
            candyPositions[i] = new Point(candyRandomX, 70);
            candySpeeds[i] = 5.0; // ความเร็วเริ่มต้นของลูกอม

            // กำหนดตำแหน่งของมือผีแบบสุ่ม
            int randomX = random.nextInt(GAME_WIDTH - 100);
            ghostHandPositions[i] = new Point(randomX, 710);
        }

        broadcastCandyAndGhostHands(); // ส่งตำแหน่งเริ่มต้นให้ผู้เล่นทุกคน

        while (true) {
            moveCandy(); // ขยับลูกอมลงมา

            try {
                Thread.sleep(50); // ควบคุมความเร็วในการอัพเดทตำแหน่งลูกอม
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveCandy() {
        for (int i = 0; i < MAX_CANDIES; i++) {
            candyPositions[i].y += candySpeeds[i]; // ขยับลูกอมลงตามความเร็วของมัน

            // ถ้าลูกอมตกลงไปนอกจอด้านล่าง ให้รีเซ็ตตำแหน่ง
            if (candyPositions[i].y > GAME_HEIGHT) {
                candyPositions[i].y = 70; // รีเซ็ตตำแหน่ง Y
                candyPositions[i].x = random.nextInt(GAME_WIDTH - 100); // รีเซ็ตตำแหน่ง X แบบสุ่ม
                candySpeeds[i] = 5.0; // รีเซ็ตความเร็ว
            }
        }
        broadcastCandyAndGhostHands(); // ส่งตำแหน่งที่อัพเดทแล้วให้ผู้เล่นทุกคน
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
