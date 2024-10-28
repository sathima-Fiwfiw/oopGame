import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    private Map<String, PlayerInfo> playerScores = new HashMap<>();
    private static Set<String> playerNames = new HashSet<>();
    private static Map<String, Integer> playerCharacters = new HashMap<>();
    // private static List<ClientHandler> clientHandlers; // รายชื่อผู้เล่นทั้งหมด
    tradetime timecount;
    ThreadRain threadRain; // เพิ่มการใช้งาน ThreadRain
    ThreadHand handthread;
    showReady ready;
    private boolean gameStarted = false; // ตัวแปรเพื่อเช็คว่าเกมเริ่มหรือยัง

    GameServer() {
        ready = new showReady();
        timecount = new tradetime(0, 50);
        threadRain = new ThreadRain(timecount); // เรียกใช้งาน ThreadRain
        handthread = new ThreadHand(timecount);
    }

    public static void main(String[] args) {
        System.out.println("Game Server is running...");
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + ip.getHostAddress());
            new GameServer().startServer(); // เรียกใช้เมธอดเริ่มเซิร์ฟเวอร์
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameStarted(boolean started) {
        this.gameStarted = started;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            // boolean firstClientConnected = false;

            while (true) {
                Socket socket = serverSocket.accept(); // รอการเชื่อมต่อจากไคลเอนต์
                new ClientHandler(socket).start(); // เริ่มเธรดใหม่สำหรับการจัดการไคลเอนต์
                // ตรวจสอบว่าถ้ามี client เข้ามาเชื่อมต่อแล้วค่อยเริ่ม
                if (gameStarted) {
                    startBroadcastingReady();
                }

                // firstClientConnected = true; // ตั้งค่าเป็น true หลังจาก client แรกเชื่อมต่อ
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ฟังก์ชันเริ่มเกม
    private void startGame() {
        if (!gameStarted) {
            setGameStarted(true);
            System.out.println("Game has started!"); // แสดงข้อความในคอนโซล
            // เพิ่มโค้ดเริ่มเกมที่คุณต้องการที่นี่
        }
    }

    private void startBroadcastingReady() {
        ready.start(); // เริ่มการทำงานของเธรดที่แสดง Ready

        new Thread(() -> {
            try {
                // วนลูปตรวจสอบสถานะการแสดง Ready
                while (ready.showReadyImage) {

                    Thread.sleep(100); // ส่งข้อมูลทุกๆ 100 มิลลิวินาที
                    broadcastReady();
                }

                // เมื่อ ready เสร็จแล้วก็เริ่ม Broadcast อย่างอื่นต่อ
                startBroadcastingTime();
                startBroadcastingcandy();
                startBroadcastingHand();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void broadcastReady() {
        String ReadyMessage = "Ready," + ready.showReadyImage;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(ReadyMessage);
            }
        }
    }

    private void startBroadcastingTime() {
        timecount.startdown(); // เริ่มนับถอยหลัง
        new Thread(() -> {
            while (timecount.minutes > 0 || timecount.seconds > 0) { // ตรวจสอบให้แน่ใจว่ายังไม่นับถึง 00:00
                try {
                    broadcastTime(timecount.minutes, timecount.seconds);
                    Thread.sleep(1000); // ส่งข้อมูลทุกวินาที
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // นับถอยหลังจนเหลือ 00:00 แล้วจึงส่ง broadcastTimeEnd
            broadcastTime(0, 0); // ส่งเวลา 00:00 ให้ client

            try {
                Thread.sleep(10); // ดีเลย์ก่อนส่งสัญญาณสิ้นสุด
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            broadcastTimeEnd(timecount.isend);

        }).start();
    }

    private void broadcastTime(int minutes, int seconds) {
        String timeMessage = "time," + minutes + "," + seconds;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(timeMessage);
            }
        }
    }

    private void broadcastTimeEnd(boolean isend) {
        String timeendMessage = "timeend," + isend;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(timeendMessage);
            }
        }
    }

    private void startBroadcastingcandy() {
        threadRain.start();
        new Thread(() -> {
            while (timecount.isend) {
                try {

                    for (int i = 0; i < threadRain.Candy; i++) {
                        broadcastCandyPosition(); // ส่งตำแหน่งลูกอมทุกวินาทีด้วย
                    }
                    broadcastDonutPosition();
                    broadcastPumpkinPosition();

                    Thread.sleep(20); // ส่งข้อมูลทุกวินาที
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
    private void broadcastCandyPosition() {
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                for (int i = 0; i < threadRain.Candy; i++) {
                    String candyMessage = "candy," + i + "," + threadRain.Ranx[i] + "," +
                            threadRain.Rany[i] + "," + threadRain.ranspeed[i] + "," + threadRain.iscandy[i];
                    clientOut.println(candyMessage);
                }
            }
        }
    }

    // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
    private void broadcastDonutPosition() {
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {

                // ส่งตำแหน่งและความเร็วของโดนัทและฟักทอง
                String donutMessage = " ," + threadRain.donutX + "," + threadRain.donutY + "," + threadRain.donutSpeed
                        + "," + threadRain.isdonut;
                clientOut.println(donutMessage);

            }
        }
    }

    // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
    private void broadcastPumpkinPosition() {
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                String pumpkinMessage = "  ," + threadRain.pumpkinX + "," + threadRain.pumpkinY + ","
                        + threadRain.pumpkinSpeed + "," + threadRain.ispumpkin;
                clientOut.println(pumpkinMessage);
            }
        }
    }

    private void startBroadcastingHand() {
        handthread.start();
        new Thread(() -> {
            while (timecount.isend) {
                if (handthread.ishand) {
                    try {
                        broadcastHand(handthread.handX, handthread.handY);
                        broadcastIsHand(handthread.ishand);
                        Thread.sleep(4000); // วาดมืออยู่ 2 วินาที
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        broadcastHand(handthread.handX, handthread.handY);
                        broadcastIsHand(handthread.ishand);
                        Thread.sleep(6000); // หายไป 6 วินาที
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void broadcastHand(int handX, int handY) {
        String handMessage = "hand," + handX + "," + handY;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(handMessage);
            }
        }
    }

    private void broadcastIsHand(boolean ishand) {
        String handMessage = "handend," + ishand;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(handMessage);
            }
        }
    }

    // กระจายข้อมูลไปยังผู้เล่นคนอื่น ๆ ว่าลูกอมชิ้นนี้ถูกเก็บแล้ว
    public void broadcastCandyCollision(int index) {
        // อัปเดตสถานะ iscandy ของลูกอมชิ้นนี้
        threadRain.iscandy[index] = false;

        // สร้างข้อความที่จะแจ้งให้ผู้เล่นทุกคนทราบ
        String candyCOLLISIONMessage = "CANDY_COLLISION," + index + "," + " false";

        // ใช้ synchronized เพื่อป้องกันปัญหาการเข้าถึงพร้อมกัน
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(candyCOLLISIONMessage); // ส่งข้อความไปยัง client ทุกคน
            }
        }
    }

    // กระจายข้อมูลไปยังผู้เล่นทุกคนว่าโดนัทถูกเก็บแล้ว
    public void broadcastDonutCollision() {

        threadRain.isdonut = false;

        String donutMessage = "DONUT_COLLISION," + "false";

        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(donutMessage); // ส่งข้อมูลไปยัง client ทุกคน
            }
        }
    }

    // กระจายข้อมูลไปยังผู้เล่นทุกคนว่าโดนัทถูกเก็บแล้ว
    public void broadcastPumpkinCollision() {
        threadRain.ispumpkin = false;

        String pumpkinMessage = "PUMPKIN_COLLISION," + "false";

        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(pumpkinMessage); // ส่งข้อมูลไปยัง client ทุกคน
            }
        }
    }

    private void broadcaststart() {
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println("starting,"); // ส่งคำสั่งให้ทุกคนเริ่มเกมพร้อมกัน
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String playerName;

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

                // รับชื่อผู้เล่น
                out.println("Enter your name:");
                playerName = in.readLine();

                // รับตัวละครจากผู้เล่น
                out.println("Select your character (c01, c02, c03, c04, c05):");
                String characterCode = in.readLine();
                int characterIndex = getCharacterIndex(characterCode); // ใช้เมธอดใหม่ในการแปลงรหัส

                // ตรวจสอบรหัสตัวละคร
                if (characterIndex == -1) {
                    out.println("Invalid character selection. Please try again.");
                    return; // ออกจากฟังก์ชันถ้าตัวละครไม่ถูกต้อง
                }

                // เพิ่มผู้เล่นด้วยรหัสตัวละครที่เลือก
                playerCharacters.put(playerName, characterIndex);
                sendPlayerList(out); // ส่งข้อมูลผู้เล่นคนอื่นให้ผู้เล่นใหม่

                // แจ้งให้ผู้เล่นคนอื่นรู้ว่าเข้ามาแล้ว
                broadcast("waitplay," + playerName + "," + characterCode);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received message from client: " + message);
                    String[] parts = message.split(",");

                    if (parts[0].equals("player")) {
                        String playerNamestart = parts[1];
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        String characterCodestart = parts[4];
                        broadcastPosition(playerNamestart, x, y, characterCodestart);

                    } else if (parts[0].equals("CANDY_COLLISION")) {
                        int index = Integer.parseInt(parts[1]);
                        broadcastCandyCollision(index);

                    } else if (parts[0].equals("DONUT_COLLISION")) {
                        broadcastDonutCollision();

                    } else if (parts[0].equals("PUMPKIN_COLLISION")) {
                        broadcastPumpkinCollision();

                    } else if (parts[0].equals("SCORE")) {
                        String PlayerName2 = parts[1];
                        String CharacterCode2 = parts[2];

                        try {
                            int score = Integer.parseInt(parts[3]);
                            if (score >= 0) {
                                playerScores.put(PlayerName2, new PlayerInfo(PlayerName2, CharacterCode2, score));
                                checkWinner();
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid score format: " + parts[3]);
                        }
                    } else if (message.equalsIgnoreCase("/start")) {
                        // เริ่มเกม
                        startGame();
                        broadcaststart();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // ปิดการเชื่อมต่อ
                    synchronized (clientWriters) {
                        if (out != null) {
                            clientWriters.remove(out);
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }

        private void sendPlayerList(PrintWriter out) {
            for (String playerName : playerNames) {
                int characterIndex = playerCharacters.get(playerName);
                out.println(playerName + " has joined. Character: c0" + (characterIndex + 1));
            }
        }

        private void broadcastPosition(String playerNamestart, int x, int y, String characterCodestart) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println("player," + playerNamestart + "," + x + "," + y + "," + characterCodestart);
                }
            }
        }

        private void checkWinner() {
            String winnerName = " ";
            int highestScore = 0;
            String winnerCharacterCode = " ";

            for (Map.Entry<String, PlayerInfo> entry : playerScores.entrySet()) {
                PlayerInfo playerInfo = entry.getValue();

                if (playerInfo.score > highestScore) {
                    highestScore = playerInfo.score;
                    winnerName = playerInfo.playerName;
                    winnerCharacterCode = playerInfo.characterCode;
                }
            }

            if (winnerName != null) {
                System.out.println("Current winner is " + winnerName + " with score " + highestScore + " and character "
                        + winnerCharacterCode);

                // ส่งข้อมูลผู้ชนะกลับไปยัง client
                broadcastWinner(winnerName, highestScore, winnerCharacterCode);
            }
        }

        private void broadcastWinner(String winnerName, int winnerScore, String winnerCharacterCode) {
            String winnerMessage = "WINNER," + winnerName + "," + winnerScore + "," + winnerCharacterCode;

            synchronized (clientWriters) {
                for (PrintWriter clientOut : clientWriters) {
                    clientOut.println(winnerMessage); // ส่งข้อมูลไปยัง client ทุกคน
                }
            }
        }

        private static int getCharacterIndex(String code) {
            switch (code) {
                case "c01":
                    return 0;
                case "c02":
                    return 1;
                case "c03":
                    return 2;
                case "c04":
                    return 3;
                case "c05":
                    return 4;
                default:
                    return -1; // ถ้ารหัสไม่ถูกต้อง
            }
        }
    }

    class PlayerInfo {
        String playerName;
        String characterCode;
        int score;

        public PlayerInfo(String playerName, String characterCode, int score) {
            this.playerName = playerName;
            this.characterCode = characterCode;
            this.score = score;
        }
    }
}