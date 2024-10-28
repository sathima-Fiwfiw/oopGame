import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class WaitingRoom extends JFrame {
    private ArrayList<String> playerNames;
    private ArrayList<Image> playerCharacters;
    private JButton startButton;
    private PrintWriter out;
    private Image[] characters = new Image[5];

    public WaitingRoom() {
        playerNames = new ArrayList<>();
        playerCharacters = new ArrayList<>();

        setTitle("Waiting Room");
        setSize(1440, 810);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Start button to begin the game
        startButton = new JButton("Start Game");
        startButton.setEnabled(false); // Initially disabled
        startButton.addActionListener(new StartButtonListener());

        add(new JLabel("Players Waiting:"), BorderLayout.NORTH);
        add(new PlayerPanel(), BorderLayout.CENTER);  // Custom panel for player display
        add(startButton, BorderLayout.SOUTH);

        // Load character images
        for (int i = 0; i < characters.length; i++) {
            Image originalImage = new ImageIcon("D:/testnew/" + (i + 1) + ".png").getImage();
            int newWidth = 250;
            int newHeight = 300;
            characters[i] = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        }

        // Connect to server
        connectToServer();
    }

    private void connectToServer() {
        try {
            String serverIP = JOptionPane.showInputDialog("Enter server IP Address:", "192.168.0.101");
            Socket socket = new Socket(serverIP, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Get player name
            String playerName = JOptionPane.showInputDialog("Enter your name:");

            // Character selection
            String[] characterOptions = {"c01", "c02", "c03", "c04", "c05"};
            int characterIndex = JOptionPane.showOptionDialog(this, "Select your character:", 
                    "Character Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, characterOptions, characterOptions[0]);

            if (characterIndex == JOptionPane.CLOSED_OPTION) {
                JOptionPane.showMessageDialog(this, "You must select a character!");
                return;
            }

            out.println(playerName);
            out.println(characterOptions[characterIndex]); // Send character code to the server

            // Start a thread to listen for messages from the server
            new Thread(new ServerListener(in)).start();
           
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unknown Host: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "I/O Error: " + e.getMessage());
        }
    }

    public void addPlayer(String playerName, String characterCode) {
        playerNames.add(playerName);
        int characterIndex = getCharacterIndex(characterCode);
        playerCharacters.add(characters[characterIndex]);

        // Enable the start button if there are players
        startButton.setEnabled(!playerNames.isEmpty());

        // Refresh panel to show updated player list
        repaint();
    }

    private int getCharacterIndex(String code) {
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
                return 0;
        }
    }

    private class PlayerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int x = 50;
            int y = 50;
            int characterWidth = 200;
            int characterHeight = 300;
            int gap = 50;

            for (int i = 0; i < playerNames.size(); i++) {
                String playerName = playerNames.get(i);
                Image characterImage = playerCharacters.get(i);

                // Draw player name
                g.drawString(playerName, x, y);

                // Draw character image below the name
                g.drawImage(characterImage, x, y + 20, characterWidth, characterHeight, this);

                // Move x for the next player, wrap to new line if it exceeds the panel width
                x += characterWidth + gap;
                if (x + characterWidth > getWidth()) {
                    x = 50;
                    y += characterHeight + 50;
                }
            }
        }
    }

    private class ServerListener implements Runnable {
        private BufferedReader in;

        public ServerListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.contains("has joined")) {
                        String[] playerInfo = message.split("Character: ");
                        if (playerInfo.length == 2) {
                            String playerName = playerInfo[0].trim();
                            String characterCode = playerInfo[1].trim();
                            addPlayer(playerName, characterCode);
                        } else {
                            System.out.println("Invalid player info format: " + message);
                        }
                    } else {
                        System.out.println("Received message: " + message);
                        if (message.startsWith("Game")) {
                            JOptionPane.showMessageDialog(WaitingRoom.this, message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            out.println("/start");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WaitingRoom waitingRoom = new WaitingRoom();
            waitingRoom.setVisible(true);
        });
    }
}
