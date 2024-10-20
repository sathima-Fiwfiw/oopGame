import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient extends JFrame {
    private static final int SERVER_PORT = 12345; // พอร์ตที่ใช้ในการเชื่อมต่อกับเซิร์ฟเวอร์

    private JPanel panel;
    private int x = 100, y = 100; // starting position
    private Socket socket; // ใช้เชื่อมต่อกับเซิร์ฟเวอร์
    private PrintWriter out; // ใช้ส่งข้อมูลออกไปยังเซิร์ฟเวอร์
    private Map<String, PlayerPoint> otherPlayers = new HashMap<>(); // แผนที่สำหรับเก็บตำแหน่งของผู้เล่นคนอื่น
    private String playerName; // Name of this player
    private String characterCode; // Character code
    private String serverIP; // Server IP address
    Image bg;
    Image[] character = new Image[5];
    boolean isJumping = false;

    // Adjusted constructor to accept name, character code, and IP from Ipgame
    public GameClient(String characterCode, String playerName, String serverIP) {
        this.playerName = playerName;
        this.characterCode = characterCode;
        this.serverIP = serverIP;

        // โหลดภาพพื้นหลังและตัวละคร
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "ingame" + File.separator + "bgingame.png");

        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/oopGame/imageip/" + (i + 1) + ".png").getImage();
        }

        createAndShowGUI(); // สร้าง GUI และแสดง
        connectToServer(); // เชื่อมต่อกับเซิร์ฟเวอร์


    }

    private void createAndShowGUI() {
        setTitle("Game Client");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the current player
                g.drawImage(bg, 0, 0, this);
                g.drawImage(character[getCharacterIndex(characterCode)], x - getCharacterOffset(), y, getCharacterWidth(), 250, this); // วาดตัวผู้เล่น
                g.setColor(Color.BLACK);
                g.drawString(playerName, x, y - 5); // วาดชื่อผู้เล่นเหนือรูปตัวละคร

                // วาดผู้เล่นคนอื่น
                for (Map.Entry<String, PlayerPoint> entry : otherPlayers.entrySet()) {
                    PlayerPoint p = entry.getValue();
                    String otherName = entry.getKey();

                    Image otherCharacterImage = getCharacterImageBasedOnCode(p.characterCode); // ใช้ characterCode ของผู้เล่นอื่น
                    g.drawImage(otherCharacterImage, p. x - getCharacterOffset(), p.y, getCharacterWidth(), 250, this); // วาดตัวผู้เล่น
                    g.drawString(otherName, p.x, p.y - 5); // วาดชื่อผู้เล่นเหนือรูปตัวละคร
                }

            }
        };

        panel.setPreferredSize(new Dimension(1440, 810));
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mouse motion listener to track mouse movement
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int movecharacter = e.getX(); // Center the character on the mouse, using offset

                // ตัวละคร c01 - เช็คขอบ
            if ("c01".equals(characterCode)) {
                if (movecharacter - 100 < 0) { // ขอบซ้าย
                    movecharacter = 100;
                }
                if (movecharacter + 90 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 90;
                }
            }
            // ตัวละคร c02 - เช็คขอบ
            else if ("c02".equals(characterCode)) {
                if (movecharacter - 80 < 0) { // ขอบซ้าย
                    movecharacter = 80;
                }
                if (movecharacter + 65 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 65;
                }
            }
            // ตัวละคร c03 - เช็คขอบ
            else if ("c03".equals(characterCode)) {
                if (movecharacter - 90 < 0) { // ขอบซ้าย
                    movecharacter = 90;
                }
                if (movecharacter + 65 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 65;
                }
            }
            // ตัวละคร c04 - เช็คขอบ
            else if ("c04".equals(characterCode)) {
                if (movecharacter - 95 < 0) { // ขอบซ้าย
                    movecharacter = 95;
                }
                if (movecharacter + 30 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 30;
                }
            }
            // ตัวละคร c05 - เช็คขอบ
            else if ("c05".equals(characterCode)) {
                if (movecharacter - 125 < 0) { // ขอบซ้าย
                    movecharacter = 125;
                }
                if (movecharacter + 40 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 40;
                }
            }
                // อัปเดตตำแหน่งผู้เล่นหลังจากเช็คขอบแล้ว
                x = movecharacter;
                y = 540; // Position Y คงที่
                sendPosition(); // ส่งตำแหน่งผู้เล่นไปยังเซิร์ฟเวอร์
                panel.repaint();
            }
        });

                // เพิ่ม mouseClicked() สำหรับการตรวจจับคลิกเมาส์
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (timecount.isend) {
                    if (!isJumping) {
                        isJumping = true; // กระโดดเมื่อไม่ได้อยู่ในสถานะการกระโดด
                    }
                }
            }
        });
    }

        // เพิ่มเมธอด getCharacterWidth() และ getCharacterOffset()
        private int getCharacterWidth() {
            switch (characterCode) { 
                case "c01": return 200;
                case "c02": return 150;
                case "c03": return 150;
                case "c04": return 120;
                case "c05": return 160;
                default: return 0;
            }
        }

        private int getCharacterOffset() {
            switch (characterCode) {
                case "c01": return 100;
                case "c02": return 80;
                case "c03": return 80;
                case "c04": return 80;
                case "c05": return 80;
                default: return 0;
            }
        }


    private void connectToServer() {
        try {
            socket = new Socket(serverIP, SERVER_PORT); // เชื่อมต่อไปยังเซิร์ฟเวอร์ที่กำหนด
            out = new PrintWriter(socket.getOutputStream(), true); // สร้าง PrintWriter เพื่อส่งข้อมูลออกไปยังเซิร์ฟเวอร์
            out.println(playerName); // ส่งชื่อผู้เล่นไปยังเซิร์ฟเวอร์
            out.println(characterCode); // ส่งรหัสตัวละครไปยังเซิร์ฟเวอร์
            new Thread(this::receivePositionUpdates).start(); // สร้าง thread ใหม่เพื่อรับข้อมูลจากเซิร์ฟเวอร์
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPosition() {
        out.println(playerName + "," + x + "," + y + "," + characterCode); // ส่งชื่อผู้เล่น ตำแหน่ง x, y และโค้ดตัวละคร
    }

    private void receivePositionUpdates() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(","); // แยกข้อมูลที่ได้รับเป็นส่วนๆ
                if (parts.length == 4) {
                    String otherPlayerName = parts[0];
                    int otherX = Integer.parseInt(parts[1]);
                    int otherY = Integer.parseInt(parts[2]);
                    String otherCharacterCode = parts[3];

                    // อัปเดตตำแหน่งของผู้เล่นคนอื่น
                    PlayerPoint playerPoint = new PlayerPoint(otherX, otherY, otherCharacterCode);
                    otherPlayers.put(otherPlayerName, playerPoint); // เก็บข้อมูลผู้เล่นอื่นลงในแผนที่
                    panel.repaint(); // วาดใหม่เพื่ออัปเดตการแสดงผล
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // เมธอดช่วยในการเลือกรูปตัวละครตามรหัส
     private Image getCharacterImageBasedOnCode(String code) {
        int index = getCharacterIndex(code);
        return character[index];
    }

    private int getCharacterIndex(String code) {
        switch (code) {
            case "c02":
                return 1;
            case "c03":
                return 2;
            case "c04":
                return 3;
            case "c05":
                return 4;
            default:
                return 0;
        }
    }

     // คลาสย่อยสำหรับเก็บตำแหน่งและโค้ดตัวละครของผู้เล่น
     private static class PlayerPoint {
        int x, y;
        String characterCode;

        PlayerPoint(int x, int y, String characterCode) {
            this.x = x;
            this.y = y;
            this.characterCode = characterCode;
        }
    }
}
