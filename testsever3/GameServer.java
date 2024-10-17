import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;

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

    tradetime timecount ; // สร้างนับถอยหลังเวลา; // ตัวแปรสำหรับนับถอยหลัง
    ThreadRain candyRain;
    ThreadHand handgrost;
    GameServer(){
        timecount = new tradetime(0,50); 
        candyRain = new ThreadRain(timecount);
        handgrost = new ThreadHand(timecount);
    }
    public static void main(String[] args) {
        System.out.println("Game Server is running...");
        new GameServer().startServer(); // เรียกเมธอดที่เปิดเซิร์ฟเวอร์
    }

    public void startServer() { 
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            boolean firstClientConnected = false; // สถานะการเชื่อมต่อของ client แรก
            while (true) {
                // รับการเชื่อมต่อจาก client
                new ClientHandler(serverSocket.accept()).start();
    
                // ตรวจสอบว่าถ้ามี client เข้ามาเชื่อมต่อแล้วค่อยเริ่มจับเวลา
                if (!firstClientConnected) {
                    candyRain.start();
                    timecount.startdown(); // เริ่มนับถอยหลัง
                    handgrost.start();
                    firstClientConnected = true; // ตั้งค่าเป็น true หลังจาก client แรกเชื่อมต่อ
                }
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
               // new Thread(GameServer.this::spawnAndMoveCandy).start();

                // ส่งข้อมูลเวลาให้ผู้เล่น
                new Thread(() -> {
                    while (timecount.isend) {
                        try {
                            Thread.sleep(1000); // ส่งข้อมูลทุกวินาที
                            broadcastTime(timecount.minutes, timecount.seconds);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
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
                        broadcastCandyAndGhostHands() ;
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

        private void broadcastTime(int minutes, int seconds) {
            String timeMessage = "time," + minutes + "," + seconds;
            synchronized (clientWriters) {
                for (PrintWriter clientOut : clientWriters) {
                    clientOut.println(timeMessage);
                }
            }
        }

        private synchronized void broadcastCandyAndGhostHands() {
            Iterator<PrintWriter> iterator = clientWriters.iterator();
            while (iterator.hasNext()) {
                PrintWriter writer = iterator.next();
                try {
                    for (int i = 0; i < MAX_CANDIES; i++) {
                        writer.println("candy," + candyRain.Ranx[i] + "," + candyRain.Rany[i]);
                        writer.println("ghostHand," + handgrost.handX + "," + handgrost.handY);
                    }
                } catch (Exception e) {
                    iterator.remove(); // ถ้ามีปัญหาการส่งให้ลบ writer ตัวนี้ออกจาก clientWriters
                }
            }
        }
    }

   /*  private void spawnAndMoveCandy() {
        for (int i = 0; i < MAX_CANDIES; i++) {
            // กำหนดค่าเริ่มต้นให้กับตำแหน่งและความเร็วของลูกอม

            candyPositions[i] = new Point(candyRain.Ranx[i], candyRain.Rany[i]);
            candySpeeds[i] = candyRain.ranspeed[i]; // ความเร็วเริ่มต้นของลูกอม

            // กำหนดตำแหน่งของมือผีแบบสุ่ม
            ghostHandPositions[i] = new Point(handgrost.handX, handgrost.handY);
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
                candyPositions[i].x = candyRain.Ranx[i]; // รีเซ็ตตำแหน่ง X แบบสุ่ม
                candySpeeds[i] = candyRain.ranspeed[i]; // สุ่มความเร็วใหม่ระหว่าง 2 ถึง 5 หน่วย
            }
        }
        //broadcastCandyAndGhostHands(); // ส่งตำแหน่งที่อัพเดทแล้วให้ผู้เล่นทุกคน
    }*/

   
}

