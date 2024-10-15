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
    private Map<String, Point> otherPlayers = new HashMap<>(); // Store positions of other players by name
    private String playerName; // Name of this player
    Image bg;
    Image[] character = new Image[5];
    
    public GameClient(Color color) {
        bg = Toolkit.getDefaultToolkit().getImage("C:/testSever/bgingame.png"); 
        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/testSever/" + (i + 1) + ".png").getImage(); 
        }
        this.color = color;
        getPlayerName();
        createAndShowGUI();
        connectToServer();
    }

    private void getPlayerName() {
        playerName = JOptionPane.showInputDialog("Enter your name:");
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Player"; // Default name
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Game Client");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the current player
                Image currentCharacter = getCurrentCharacter();
                g.drawImage(bg, 0, 0, this);
                g.setColor(color);
                g.drawImage(currentCharacter, x , y, 100, 100, this); // Draw player as a circle
                g.setColor(Color.BLACK);
                g.drawString(playerName, x, y - 5); // Draw player name above the circle

                // Draw other players
                for (Map.Entry<String, Point> entry : otherPlayers.entrySet()) {
                    Image currentCharacterfrien = getCurrentCharacter();
                    
                    g.drawImage(bg, 0, 0, this);
                    g.setColor(Color.BLUE); // Color for other players
                    Point p = entry.getValue();
                    g.drawImage(currentCharacterfrien, p.x, p.y, 100, 100, this);
                    g.drawString(entry.getKey(), p.x, p.y - 5); // Draw other player's name
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
                x = e.getX() - 10; // Center the circle on the mouse
                y = e.getY() - 10;
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
            new Thread(this::receivePositionUpdates).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPosition() {
        out.println(x + "," + y);
    }

    private void receivePositionUpdates() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(",");
                String otherName = parts[0]; // Extract the name of the other player
                int otherX = Integer.parseInt(parts[1]);
                int otherY = Integer.parseInt(parts[2]);

                // Update position of other player
                otherPlayers.put(otherName, new Point(otherX, otherY));
                panel.repaint(); // Redraw the panel
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image getCurrentCharacter() {
        switch ("c01") {
            case "c01": return character[0];
            case "c02": return character[1];
            case "c03": return character[2];
            case "c04": return character[3];
            case "c05": return character[4];
            default: return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameClient(Color.RED)); // Change color as needed
    }
}
