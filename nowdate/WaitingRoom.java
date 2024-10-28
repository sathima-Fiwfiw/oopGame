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
    private String playerName;
    private String playerIP;
    private String characterID;
    Image wallpaper, decorate1, decorate2, decorate3, decorate4, decorate5, decorate6;

    // Modify constructor to take playerName, playerIP, characterID as parameters
    public WaitingRoom(String playerName, String playerIP, String characterID) {
        this.playerName = playerName;
        this.playerIP = playerIP;
        this.characterID = characterID;

        playerNames = new ArrayList<>();
        playerCharacters = new ArrayList<>();

        setTitle("Waiting Room");
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new PlayerPanel(), BorderLayout.CENTER);  // Custom panel for player display

        // Load images for background and decorations
        loadImages();

        // Load character images
        loadCharacterImages();

        // Connect to server using playerIP, playerName, and characterID
        connectToServer();
    }

    private void loadImages() {
        wallpaper = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "bgwait.png");

        decorate1 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "1.gif");

        decorate2 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "2.gif");

        decorate3 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "3.gif");

        decorate4 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "4.gif");

        decorate5 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "5.gif");

        decorate6 = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
                File.separator + "imgloadwait" + File.separator + "6.gif");
    }

    private void loadCharacterImages() {
        for (int i = 0; i < characters.length; i++) {
            String imagePath = System.getProperty("user.dir") + File.separator + "imageip" + File.separator + (i + 1) + ".png";
            // โหลดภาพด้วย Toolkit
            Image originalImage = Toolkit.getDefaultToolkit().createImage(imagePath);
            int newWidth = 250;
            int newHeight = 300;
            characters[i] = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(playerIP, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(playerName);              // Send player name to server
            out.println(characterID);             // Send character ID to server

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

        startButton.setEnabled(!playerNames.isEmpty());

        revalidate();
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
        PlayerPanel() {
            setLayout(null);
            ImageIcon start = new ImageIcon(System.getProperty("user.dir") + 
            File.separator + "imgloadwait" + File.separator + "bttstart.png");
            Image startButtonImage = start.getImage().getScaledInstance(138, 58, Image.SCALE_SMOOTH);

            startButton = new JButton(new ImageIcon(startButtonImage));
            startButton.setBounds(650, 670, 140, 60);
            startButton.setEnabled(false);
            startButton.addActionListener(new StartButtonListener()); // เชื่อมโยง listener
            add(startButton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(wallpaper, 0, 0, this);
            g.drawImage(decorate1, 390, 550, 300, 200, this);
            g.drawImage(decorate2, 1050, -70, 550, 300, this);
            g.drawImage(decorate3, 55, 664, 250, 150, this);
            g.drawImage(decorate4, 300, -35, 300, 200, this);
            g.drawImage(decorate5, 770, -25, 350, 170, this);
            g.drawImage(decorate6, -50, 20, 400, 250, this);

            int x = 200;
            int y = 280;
            int characterWidth = 180;
            int characterHeight = 250;
            int gap = 100;

            for (int i = 0; i < playerNames.size(); i++) {
                String playerName = playerNames.get(i);
                Image characterImage = playerCharacters.get(i);

                Font font = new Font("Berlin sans FB Demi", Font.BOLD, 20);
                g.setFont(font);

                g.drawString(playerName, x + 50, y);
                g.drawImage(characterImage, x, y + 20, characterWidth, characterHeight, this);

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
                        String[] playerInfo =  message.split(",");
                        if (playerInfo.length == 2) {
                            String playerName = playerInfo[0].trim();
                            String characterCode = playerInfo[1].trim();
                            addPlayer(playerName, characterCode);
                        } else {
                            System.out.println("Invalid player info format: " + message);
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
            GameClient ingame = new GameClient(characterID, playerName, playerIP);
            ingame.setVisible(true);
            dispose(); // ปิดหน้ารอ WaitingRoom
        }
    }
}
