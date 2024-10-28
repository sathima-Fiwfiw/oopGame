import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Set<String> playerNames = new HashSet<>();
    private static Map<String, Integer> playerCharacters = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Game Server Started...");
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + ip.getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        private String playerName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

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
                sendPlayerList(out); // Send existing players to the new player

                synchronized (clientWriters) {
                    clientWriters.add(out);
                    playerNames.add(playerName);
                }

                // แจ้งให้ผู้เล่นคนอื่นรู้ว่าเข้ามาแล้ว
                broadcast(playerName + " has joined. Character: " + characterCode);

                // รับข้อความจากผู้เล่น
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/start")) {
                        // เริ่มเกม
                        broadcast("Game is starting!");
                        break;
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
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                    playerNames.remove(playerName);
                    broadcast(playerName + " has left the waiting room.");
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
