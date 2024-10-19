import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Map; 
import java.awt.Font;

public class GameClient extends JFrame {
    private static final int SERVER_PORT = 12345; // พอร์ตที่ใช้ในการเชื่อมต่อกับเซิร์ฟเวอร์

    private JPanel panel;
    int movecharacter = 0, characterY = 540;
    private Socket socket; // ใช้เชื่อมต่อกับเซิร์ฟเวอร์
    private PrintWriter out; // ใช้ส่งข้อมูลออกไปยังเซิร์ฟเวอร์
    private Map<String, PlayerPoint> otherPlayers = new HashMap<>(); // แผนที่สำหรับเก็บตำแหน่งของผู้เล่นคนอื่น
    private String playerName; // Name of this player
    private String characterCode; // Character code
    private String serverIP; // Server IP address
    Image bg, timeUP;
    Image[] character = new Image[5];
    boolean isJumping = false;
    Timer actionTimer;
    private int remainingMinutes;
    private int remainingSeconds;
    boolean remainEnd = true;
    int Candys = 5;
    Image[] Candy =new Image[Candys];
    int posicandyX[] =new int[Candys];
    int posicandyY[] =new int[Candys];
    double speedcandy[] =new double[Candys];
    int indexcandy;
    

    // Adjusted constructor to accept name, character code, and IP from Ipgame
    public GameClient(String characterCode, String playerName, String serverIP) {
        this.playerName = playerName;
        this.characterCode = characterCode;
        this.serverIP = serverIP;

        // โหลดภาพพื้นหลังและตัวละคร
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "ingame" + File.separator + "bgingame.png");

        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:\\oopGame\\imageip/" + (i + 1) + ".png").getImage(); 
        }
        for (int i = 0; i < Candys; i++) {
            Candy[i] = new ImageIcon("C:\\oopGame\\imageRain/" + ((i % 10) + 1)  + ".png").getImage(); 
        }

        timeUP = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "time.png");

        createAndShowGUI(); // สร้าง GUI และแสดง
        connectToServer(); // เชื่อมต่อกับเซิร์ฟเวอร์

        actionTimer = new Timer(15, new ActionListener() {
               boolean jumpingUp = true; // ทิศทางการกระโดด
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isJumping) {
                    if (jumpingUp) {
                        characterY -= 10; // Move up
                        if (characterY <= 385) {
                            jumpingUp = false; // Change direction when reaching peak
                        }
                    } else {
                        characterY += 10; // Move down

                        if (characterY >= 540) { // Landing on the ground
                            characterY = 540;
                            isJumping = false;
                            jumpingUp = true; // Reset jump direction
                        }
                    }

                }
                sendPosition(); 
                panel.repaint(); // Repaint the panel every tick
            }
        });
        actionTimer.start(); // Start the timer

    }

    private void createAndShowGUI() {
        setTitle("Game Client");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the current player
                g.drawImage(bg, 0, 0, this);
                g.drawImage(character[getCharacterIndex(characterCode)], movecharacter - getCharacterOffset(characterCode), characterY, getCharacterWidth(characterCode), 250, this); // วาดตัวผู้เล่น
                g.setColor(Color.BLACK);
                Font font = new Font("Berlin sans FB Demi", Font.BOLD, 20); 
                g.setFont(font); 
                g.drawString(playerName, movecharacter - getCharacterOffset(characterCode) + 50 , characterY - 20); // วาดชื่อผู้เล่นเหนือรูปตัวละคร
            
                  // วาดลูกอมที่ตำแหน่งที่ได้รับจากเซิร์ฟเวอร์
                for (int i = 0; i < Candys; i++) {
                    g.drawImage(Candy[i], posicandyX[i], posicandyY[i], 60, 35, this);
                }

                // วาดผู้เล่นคนอื่น
                for (Map.Entry<String, PlayerPoint> entry : otherPlayers.entrySet()) {
                    PlayerPoint p = entry.getValue();
                    String otherName = entry.getKey();

                    Image otherCharacterImage = getCharacterImageBasedOnCode(p.characterCode); // ใช้ characterCode ของผู้เล่นอื่น
                    g.drawImage(otherCharacterImage, p.x - getCharacterOffset(p.characterCode), p.y, getCharacterWidth(p.characterCode), 250, this); // วาดตัวผู้เล่น
                    g.setFont(font); 
                    g.drawString(otherName, p.x - getCharacterOffset(p.characterCode) + 50, p.y - 20); // วาดชื่อผู้เล่นเหนือรูปตัวละคร
                }

                // Draw the current time
                Font fonttime = new Font("Berlin sans FB Demi", Font.BOLD, 40); 
                g.setFont(fonttime);
                g.drawString(String.format("%02d:%02d", remainingMinutes, remainingSeconds), 300, 55);

                if (!remainEnd) {
                    g.drawImage(timeUP, 320, 100, 800, 500, this);
                }
                repaint();
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
                movecharacter = e.getX(); // Center the character on the mouse, using offset

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
                if (movecharacter + 70 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 70;
                }
            }
            // ตัวละคร c03 - เช็คขอบ
            else if ("c03".equals(characterCode)) {
                if (movecharacter - 81 < 0) { // ขอบซ้าย
                    movecharacter = 81;
                }
                if (movecharacter + 67 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 67;
                }
            }
            // ตัวละคร c04 - เช็คขอบ
            else if ("c04".equals(characterCode)) {
                if (movecharacter - 83 < 0) { // ขอบซ้าย
                    movecharacter = 83;
                }
                if (movecharacter + 45 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 45;
                }
            }
            // ตัวละคร c05 - เช็คขอบ
            else if ("c05".equals(characterCode)) {
                if (movecharacter - 80 < 0) { // ขอบซ้าย
                    movecharacter = 80;
                }
                if (movecharacter + 70 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 70;
                }
            }
              
                sendPosition(); // ส่งตำแหน่งผู้เล่นไปยังเซิร์ฟเวอร์
                panel.repaint();
            }
        });

                // เพิ่ม mouseClicked() สำหรับการตรวจจับคลิกเมาส์
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    if (!isJumping) {
                        isJumping = true; // กระโดดเมื่อไม่ได้อยู่ในสถานะการกระโดด
                    }
            }
        });
    }

        // เพิ่มเมธอด getCharacterWidth() และ getCharacterOffset()
        private int getCharacterWidth(String characterCodeID) {
            switch (characterCodeID) { 
                case "c01": return 200;
                case "c02": return 150;
                case "c03": return 150;
                case "c04": return 120;
                case "c05": return 160;
                default: return 0;
            }
        }

        private int getCharacterOffset(String characterCodeID) {
            switch (characterCodeID) {
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
        out.println(playerName + "," + movecharacter + "," + characterY + "," + characterCode); // ส่งชื่อผู้เล่น ตำแหน่ง x, y และโค้ดตัวละคร
    }

    private void updateTimerDisplay(int minutes, int seconds) {
        this.remainingMinutes = minutes;
        this.remainingSeconds = seconds;
    }

    private void updateCandyPosition(int x, int y,int index,double speed) {
        this.posicandyX[index] = x;
        this.speedcandy[index] = speed;
        this.posicandyY[index] = (int) (y + speedcandy[index]); 
        panel.repaint(); // Repaint the panel to update candy position
    }

    private void updatetimeend(boolean isend){
        this.remainEnd = isend;
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
                else if (parts[0].equals("time")) {
                    // Update time
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);
                    updateTimerDisplay(minutes, seconds); // Update the timer display
                }  else if (parts[0].equals("candy")) {
                    // อัปเดตตำแหน่งและความเร็วลูกอมที่ได้รับจากเซิร์ฟเวอร์
                    int candyIndex = Integer.parseInt(parts[1]); // รับตำแหน่ง index ของลูกอม
                    int candyX = Integer.parseInt(parts[2]); // รับตำแหน่ง x
                    int candyY = Integer.parseInt(parts[3]); // รับตำแหน่ง y
                    double candySpeed = Double.parseDouble(parts[4]); // รับความเร็วของลูกอม
                
                    // อัปเดตตำแหน่งลูกอมในแผงหรือพื้นที่แสดงผล
                    updateCandyPosition(candyX, candyY, candyIndex,candySpeed);
                    panel.repaint();
                } else if (parts[0].equals("timeend")) {
                    // อัปเดตตำแหน่งและความเร็วลูกอมที่ได้รับจากเซิร์ฟเวอร์
                    boolean isend = Boolean.parseBoolean(parts[1]); // รับตำแหน่ง index ของลูกอม
                    updatetimeend(isend);
                    panel.repaint();
                }
                panel.repaint(); 
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
        PlayerPoint(int movecharacter, int characterY, String characterCode) {
            this.x = movecharacter;
            this.y = characterY;
            this.characterCode = characterCode;
        }
    }
}
