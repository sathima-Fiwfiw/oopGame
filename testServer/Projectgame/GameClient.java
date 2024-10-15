import javax.swing.*;
import java.awt.*;
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
    private String playerName; // Name of this player
    private String characterCode; // Character code
    Image bg;
    Image[] character = new Image[5];

    public GameClient(Color color) {
        bg = Toolkit.getDefaultToolkit().getImage("C:/testSever/bgingame.png");
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
                g.drawImage(character[getCharacterIndex(characterCode)], x, y, 100, 100, this); // Draw player
                g.setColor(Color.BLACK);
                g.drawString(playerName, x, y - 5); // Draw player name above the character

                // Draw other players
                for (Map.Entry<String, PlayerPoint> entry : otherPlayers.entrySet()) {
                    PlayerPoint p = entry.getValue();
                    String otherName = entry.getKey();

                    Image otherCharacterImage = getCharacterImageBasedOnCode(p.characterCode); // Use characterCode from PlayerPoint
                    g.drawImage(otherCharacterImage, p.x, p.y, 100, 100, this);
                    g.drawString(otherName, p.x, p.y - 5); // Draw other player's name
                }
            }
        };

        panel.setPreferredSize(new Dimension(400, 400));
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Mouse motion listener to track mouse movement
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX() - 50; // Center the character on the mouse
                y = e.getY() - 50;
                sendPosition();
                panel.repaint();
            }
        });
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
                if (parts.length == 4) { // Check length
                    String otherName = parts[0]; // Extract the name of the other player
                    int otherX = Integer.parseInt(parts[1]);
                    int otherY = Integer.parseInt(parts[2]);
                    String otherCharacterCode = parts[3]; // Extract character code

                    // Update position and character code of other player
                    otherPlayers.put(otherName, new PlayerPoint(otherX, otherY, otherCharacterCode)); // Use PlayerPoint
                    panel.repaint(); // Redraw the panel
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image getCharacterImageBasedOnCode(String code) {
        switch (code) {
            case "c01": return character[0];
            case "c02": return character[1];
            case "c03": return character[2];
            case "c04": return character[3];
            case "c05": return character[4];
            default: return null; // Use default image
        }
    }

    private int getCharacterIndex(String code) {
        switch (code) {
            case "c01": return 0;
            case "c02": return 1;
            case "c03": return 2;
            case "c04": return 3;
            case "c05": return 4;
            default: return 2; // Default to the 3rd character
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClient(Color.RED)); // Change color as needed
    }

    // Class PlayerPoint
    private class PlayerPoint {
        int x, y;
        String characterCode;

        public PlayerPoint(int x, int y, String characterCode) {
            this.x = x;
            this.y = y;
            this.characterCode = characterCode;
        }
    }
}
