import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient extends JFrame {
    private static final int SERVER_PORT = 12345;

    private JPanel panel;
    private int x = 100, y = 100; // starting position
    private Socket socket;
    private PrintWriter out;
    private Map<String, PlayerPoint> otherPlayers = new HashMap<>(); // Store positions of other players by name
    private String playerName; // Name of this player
    private String characterCode; // Character code
    private String serverIP; // Server IP address
    Image bg;
    Image[] character = new Image[5];

    // Adjusted constructor to accept name, character code, and IP from Ipgame
    public GameClient(String characterCode, String playerName, String serverIP) {
        this.playerName = playerName;
        this.characterCode = characterCode;
        this.serverIP = serverIP;

        // Load background and character images
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "ingame" + File.separator + "bgingame.png");

        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/oopGame/imageip/" + (i + 1) + ".png").getImage();
        }

        createAndShowGUI();
        connectToServer();
    }

    private void createAndShowGUI() {
        setTitle("Game Client");
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
            }
        };

        panel.setPreferredSize(new Dimension(1440, 810));
        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

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
    }

    private void connectToServer() {
        try {
            socket = new Socket(serverIP, SERVER_PORT);
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
                    PlayerPoint playerPoint = new PlayerPoint(otherX, otherY, otherCharacterCode);
                    otherPlayers.put(otherPlayerName, playerPoint);
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

}
