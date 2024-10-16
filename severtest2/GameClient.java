import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

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

    public GameClient(Color color) {
        bg = Toolkit.getDefaultToolkit().getImage("C:/testSever/bgingame.png");
        hand = Toolkit.getDefaultToolkit().getImage("C:/testSever/handgrost.png");
        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/testSever/" + (i + 1) + ".png").getImage();
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

                // Draw single candy
                g.setColor(Color.YELLOW); // Candy color
                g.fillOval(candyPosition.x, candyPosition.y, 20, 20); // Adjust candy size as needed

                // Draw ghost hand if visible
                if (ghostHandVisible) {
                    g.drawImage(hand, ghostHandPosition.x, ghostHandPosition.y, 80, 100, this); // Adjust ghost hand size as needed
                }
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
        new Timer(5000, e -> {
            ghostHandVisible = true;
            ghostHandPosition.x = (int) (Math.random() * panel.getWidth());
            ghostHandPosition.y = (int) (Math.random() * (panel.getHeight() - 100));
            panel.repaint();

            // Hide the ghost hand after 2 seconds
            new Timer(2000, e2 -> {
                ghostHandVisible = false;
                panel.repaint();
            }).setRepeats(false);
        }).start();
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
                    otherPlayers.put(otherPlayerName, new PlayerPoint(otherX, otherY, otherCharacterCode));
                    panel.repaint();
                } else if (parts[0].equals("candy")) {
                    // Update single candy position
                    int candyX = Integer.parseInt(parts[1]);
                    int candyY = Integer.parseInt(parts[2]);
                    candyPosition.setLocation(candyX, candyY);

                    // Reset candy when it reaches the bottom
                    if (candyPosition.y >= panel.getHeight()) {
                        candyPosition.y = 70; // Reset to top
                        candyPosition.x = (int) (Math.random() * panel.getWidth()); // Random x-position
                    }

                    panel.repaint();
                }
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
                return 0;
        }
    }

    // A simple class to store player positions and character codes
    private static class PlayerPoint {
        int x, y;
        String characterCode;

        PlayerPoint(int x, int y, String characterCode) {
            this.x = x;
            this.y = y;
            this.characterCode = characterCode;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClient(Color.RED)); // Change player color as needed
    }
}
