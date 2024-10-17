import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class GameClient {
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JPanel panel;
    private int x = 100, y = 100; // starting position
    private Color color;
    private Socket socket;
    private PrintWriter out;
    private Map<String, PlayerPoint> otherPlayers = new HashMap<>(); // Store positions of other players by name
    private Point candyPosition = new Point(); // Store single candy position
    private Point ghostHandPosition = new Point(); // Store single ghost hand position
    private boolean ghostHandVisible = false; // Control visibility of ghost hand
    private String playerName; // Name of this player
    private String characterCode; // Character code
    Image bg, hand;
    Image[] character = new Image[5];
    int Candy = 10;   
    Image[] CandyRain = new Image[Candy];
    private List<Point> candyPositions = new ArrayList<>();
    private int remainingMinutes;
    private int remainingSeconds;

    public GameClient(Color color) {
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "bgingame.png");
        hand = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") + File.separator + "handgrost.png");
        for (int i = 0; i < character.length; i++) {
            character[i] = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +  File.separator + (i + 1) + ".png"); 
        }
        for (int i = 0; i < Candy; i++) {
            CandyRain[i] = Toolkit.getDefaultToolkit().createImage(
                    System.getProperty("user.dir") + File.separator + "imageRain" + File.separator + (i + 1) + ".png");
        }
    
        this.color = color;
        getPlayerName();
        getCharacterCode(); // รับรหัสตัวละคร
        createAndShowGUI();
        connectToServer();
    }

    private void getPlayerName() {
        playerName = JOptionPane.showInputDialog("Enter your name:");
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Player"; // Default name
        }
    }

    private void getCharacterCode() {
        String[] characterOptions = {"c01", "c02", "c03", "c04", "c05"};
        characterCode = (String) JOptionPane.showInputDialog(
                frame,
                "Choose your character code:",
                "Character Code Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                characterOptions,
                characterOptions[0] // Default selection
        );

        if (characterCode == null) {
            characterCode = "c01"; // Default character code
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Game Client");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the current player
                g.drawImage(bg, 0, 0, this);
                g.drawImage(character[getCharacterIndex(characterCode)], x, y, 180, 250, this); // Draw player
                g.setColor(Color.BLACK);
                g.drawString(playerName, x, y - 5); // Draw player name above the character

                // Draw other players
                for (Map.Entry<String, PlayerPoint> entry : otherPlayers.entrySet()) {
                    PlayerPoint p = entry.getValue();
                    String otherName = entry.getKey();

                    Image otherCharacterImage = getCharacterImageBasedOnCode(p.characterCode); // Use characterCode from PlayerPoint
                    g.drawImage(otherCharacterImage, p.x, p.y, 180, 250, this);
                    g.drawString(otherName, p.x, p.y - 5); // Draw other player's name
                }

                int maxCandiesToDraw = Math.min(10, candyPositions.size()); // จำกัดจำนวนลูกอมที่จะแสดงไม่เกิน 10
                for (int i = 0; i < maxCandiesToDraw; i++) {
                    Point candyPos = candyPositions.get(i);
                    if (candyPos.x >= 0 && candyPos.x <= panel.getWidth() && 
                        candyPos.y >= 0 && candyPos.y <= panel.getHeight()) {
                        
                        // ใช้ i % CandyRain.length เพื่อให้แน่ใจว่า i ไม่เกินขนาดของ CandyRain
                        int candyImageIndex = i % CandyRain.length; 
                        System.out.println("Drawing candy at: " + candyPos.x + ", " + candyPos.y);
                        
                        g.drawImage(CandyRain[candyImageIndex], candyPos.x, candyPos.y, 60, 35, this); 
                    } else {
                        System.out.println("Candy position out of bounds: " + candyPos.x + ", " + candyPos.y);
                    }
                }

                // Draw ghost hand if visible for current player
                if (ghostHandVisible) {
                    g.drawImage(hand, ghostHandPosition.x, ghostHandPosition.y, 80, 100, this); // Draw ghost hand
                }

                 // Draw the current time
                 g.setColor(Color.BLACK);
                 g.setFont(new Font("Arial", Font.BOLD, 24));
                 g.drawString(String.format("%02d:%02d", remainingMinutes, remainingSeconds), 300, 55);
            }
        };

        panel.setPreferredSize(new Dimension(1440, 810));
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Mouse motion listener to track mouse movement
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX() - 80; // Center the character on the mouse
                y = 540;
                sendPosition();
                panel.repaint();
            }
        });

        // Start the ghost hand timer to show the hand periodically
        Timer ghostHandTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ghostHandVisible = true; // Make ghost hand visible
                ghostHandPosition.setLocation((int) (Math.random() * panel.getWidth()), (int) (Math.random() * panel.getHeight())); // Random position
                sendGhostHandPosition();
                panel.repaint();
            }
        });
        ghostHandTimer.start();
    }

    private void connectToServer() {
        String serverAddress = JOptionPane.showInputDialog("Enter server IP address:");
        try {
            socket = new Socket(serverAddress, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(playerName); // Send player name to the server
            out.println(characterCode); // Send character code to the server
            new Thread(this::receivePositionUpdates).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPosition() {
        out.println(playerName + "," + x + "," + y + "," + characterCode); // Include player name and character code
    }

    private void sendGhostHandPosition() {
        out.println("ghostHand," + ghostHandPosition.x + "," + ghostHandPosition.y);
    }

    private void updateTimerDisplay(int minutes, int seconds) {
        this.remainingMinutes = minutes;
        this.remainingSeconds = seconds;
    }

    private void receivePositionUpdates() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(",");
                if (parts.length == 4) {
                    String otherPlayerName = parts[0];
                    int otherX = Integer.parseInt(parts[1]);
                    int otherY = Integer.parseInt(parts[2]);
                    String otherCharacterCode = parts[3];
    
                    // Update positions of other players
                    PlayerPoint playerPoint = new PlayerPoint(otherX, otherY, otherCharacterCode);
                    otherPlayers.put(otherPlayerName, playerPoint);
                    panel.repaint();
                } else if (parts[0].equals("candy")) {
                    candyPositions.clear(); // ล้างรายการลูกอมเก่าก่อน
                    for (int i = 1; i < parts.length; i += 2) {
                        int candyX = Integer.parseInt(parts[i]);
                        int candyY = Integer.parseInt(parts[i + 1]);
                        candyPositions.add(new Point(candyX, candyY)); // เพิ่มตำแหน่งลูกอมแต่ละลูก
                        System.out.println("Received candy position: " + candyX + ", " + candyY);
                    }
                } else if (parts[0].equals("ghostHand")) {
                    // Update ghost hand position
                    int ghostHandX = Integer.parseInt(parts[1]);
                    int ghostHandY = Integer.parseInt(parts[2]);
                    ghostHandPosition.setLocation(ghostHandX, ghostHandY); // Update ghost hand position
                    ghostHandVisible = true; // Make ghost hand visible
                } else if (parts[0].equals("hideGhostHand")) {
                    ghostHandVisible = false; // ทำให้มือผีไม่ปรากฏ
                }
                else if (parts[0].equals("time")) {
                    // อัปเดตเวลา
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);
                    updateTimerDisplay(minutes, seconds); // อัปเดตการแสดงผลของเวลา
                }

                panel.repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to get the character image based on code
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
                return 0; // Return index for "c01"
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClient(Color.BLUE));
    }

    // Class to store other player's position and character code
    private static class PlayerPoint {
        int x;
        int y;
        String characterCode;

        PlayerPoint(int x, int y, String characterCode) {
            this.x = x;
            this.y = y;
            this.characterCode = characterCode;
        }
    }
}
