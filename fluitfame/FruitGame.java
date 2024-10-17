import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class FruitGame extends JPanel implements ActionListener, KeyListener {
    private Basket basket;
    private ArrayList<Fruit> fruits;
    private Timer timer;
    private int score;
    private Socket socket;
    private PrintWriter out;
    private String playerName; // ชื่อผู้เล่น
    private ArrayList<PlayerInfo> players; // ข้อมูลผู้เล่นอื่น ๆ

    public FruitGame(String ip, String name) {
        this.playerName = name; // กำหนดชื่อผู้เล่น
        basket = new Basket();
        fruits = new ArrayList<>();
        players = new ArrayList<>(); // สร้างรายการผู้เล่น
        timer = new Timer(100, this);
        timer.start();
        setFocusable(true);
        addKeyListener(this);
        score = 0;
        connectToServer(ip); // เชื่อมต่อเซิร์ฟเวอร์ด้วย IP
    }

    private void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Player: " + playerName); // ส่งชื่อผู้เล่นไปยังเซิร์ฟเวอร์
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        basket.draw(g); // วาดตะกร้าของผู้เล่น

        // วาดตะกร้าของผู้เล่นคนอื่น
        for (PlayerInfo player : players) {
            player.draw(g);
        }

        for (Fruit fruit : fruits) {
            fruit.draw(g);
        }
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20); // แสดงคะแนน
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Math.random() < 0.1) {
            fruits.add(new Fruit());
        }
        for (int i = 0; i < fruits.size(); i++) {
            fruits.get(i).fall();
            if (fruits.get(i).getY() > getHeight()) {
                fruits.remove(i);
                i--;
            } else if (fruits.get(i).intersects(basket)) {
                score += fruits.get(i).getPoints();
                out.println("Score: " + score); // ส่งคะแนนไปยังเซิร์ฟเวอร์
                fruits.remove(i);
                i--;
            }
        }
        repaint();
    }

    public void updateOtherPlayers(ArrayList<PlayerInfo> players) {
        this.players = players; // รับข้อมูลผู้เล่นคนอื่น
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            basket.moveLeft();
            out.println("Move: " + basket.getX()); // ส่งตำแหน่งตะกร้าไปยังเซิร์ฟเวอร์
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            basket.moveRight();
            out.println("Move: " + basket.getX()); // ส่งตำแหน่งตะกร้าไปยังเซิร์ฟเวอร์
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fruit Collecting Game");
        String playerName = JOptionPane.showInputDialog("Enter your name:"); // ใส่ชื่อผู้เล่น
        String ip = JOptionPane.showInputDialog("Enter server IP:"); // ใส่ IP ของเซิร์ฟเวอร์
        FruitGame game = new FruitGame(ip, playerName);
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class Basket {
    private int x;
    private final int width = 100;
    private final int height = 20;

    public Basket() {
        this.x = 400; // ตำแหน่งเริ่มต้น
    }

    public void moveLeft() {
        if (x > 0) x -= 10; // ขยับซ้าย
    }

    public void moveRight() {
        if (x < 800 - width) x += 10; // ขยับขวา
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, 550, width, height); // วาดตะกร้า
    }

    public int getX() {
        return x; // คืนค่าตำแหน่ง X
    }

    public Rectangle getBounds() {
        return new Rectangle(x, 550, width, height); // คืนค่าพื้นที่ของตะกร้า
    }
}

class Fruit {
    private int x;
    private int y;
    private final int size = 20;

    public Fruit() {
        this.x = (int) (Math.random() * (800 - size)); // ตำแหน่งเริ่มต้น
        this.y = 0;
    }

    public void fall() {
        y += 5; // ผลไม้ตกลง
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, size, size); // วาดผลไม้
    }

    public boolean intersects(Basket basket) {
        return getBounds().intersects(basket.getBounds()); // ตรวจสอบการชน
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size); // คืนค่าพื้นที่ของผลไม้
    }

    public int getY() {
        return y; // คืนค่าตำแหน่ง Y
    }

    public int getPoints() {
        return 10; // คะแนนที่ได้จากการเก็บผลไม้
    }
}

// ข้อมูลผู้เล่น
class PlayerInfo {
    private String name;
    private int x;

    public PlayerInfo(String name, int x) {
        this.name = name;
        this.x = x;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, 550, 100, 20); // วาดตะกร้าของผู้เล่น
        g.setColor(Color.BLACK);
        g.drawString(name, x, 540); // แสดงชื่อผู้เล่น
    }

    public int getX() {
        return x; // คืนค่าตำแหน่ง X
    }
}

// เซิร์ฟเวอร์ที่จะจัดการการเชื่อมต่อและคะแนนของผู้เล่น
class GameServer {
    private static final int PORT = 12345;
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game server started...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String playerName;
        private int basketX;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("Player:")) {
                        playerName = inputLine.split(": ")[1];
                        System.out.println(playerName + " has joined the game.");
                        updateOtherClients();
                    } else if (inputLine.startsWith("Move:")) {
                        basketX = Integer.parseInt(inputLine.split(": ")[1]);
                        updateOtherClients();
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
            }
        }

        private void updateOtherClients() {
            for (ClientHandler handler : clientHandlers) {
                if (handler != this) {
                    handler.out.println("PlayerInfo: " + playerName + ", " + basketX); // ส่งข้อมูลผู้เล่นให้เพื่อน
                }
            }
        }
    }
}
