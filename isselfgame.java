import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

// คลาส PlayerData สำหรับเก็บข้อมูลของผู้เล่น
class PlayerData {
    private String name;
    private int x;
    private int y;
    private int characterIndex;

    public PlayerData(String name, int x, int y, int characterIndex) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.characterIndex = characterIndex;
    }
    

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCharacterIndex() {
        return characterIndex;
    }
}

// คลาสหลักของเกม
public class itselfgame extends JFrame {
    String textcha;
    String name;
    PanelGame pInGame;
    String ip;

    itselfgame(String textcha, String name, String ip) {
        this.textcha = textcha;
        this.name = name;
        this.ip = ip;
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pInGame = new PanelGame(this);
        add(pInGame);
    }
}

// คลาสสำหรับวาดเกม
class PanelGame extends JPanel {
    itselfgame ingame;
    Image[] character = new Image[5];
    Image bg;
    int x;
    int y;
    int nameX;
    int nameY;
    private List<PlayerData> otherPlayers = new ArrayList<>(); // สร้างลิสต์สำหรับเก็บข้อมูลผู้เล่นคนอื่น

    PanelGame(itselfgame ingame) {
        this.ingame = ingame;

        bg = Toolkit.getDefaultToolkit().getImage("D://final_oopgame/bgingmae.png");

        for (int i = 0; i < 5; i++) {
            character[i] = new ImageIcon("D://final_oopgame//imagecharacter/" + (i + 1) + ".png").getImage();
        }
               // เชื่อมต่อกับเซิร์ฟเวอร์
               ClientSocket clientSocket = new ClientSocket(ingame.ip, 12345);
               try {
                   clientSocket.connect();
                   clientSocket.listenForMessages();
                
               } catch (IOException ex) {
                   ex.printStackTrace();
               }

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // คุณสามารถเพิ่มการจัดการสำหรับการลากที่นี่ได้
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX() - 350; // ปรับตำแหน่งตัวละครให้ดีขึ้น
                y = 200; // คงที่ในแนวตั้ง
                repaint(); // เรียก repaint เมื่อเมาส์เคลื่อนที่
                nameX = e.getX(); // อัพเดทตำแหน่ง X ของชื่อผู้เล่น
                nameY = 350;
                
            }
        });
    }

    public void updateOtherPlayers(List<PlayerData> players) {
        this.otherPlayers = players; // อัปเดตข้อมูลผู้เล่นคนอื่น
        System.out.println("Other players updated: " + players.size()); // ตรวจสอบจำนวนผู้เล่นที่อัปเดต
        repaint();
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);

        // วาดตัวละครของผู้เล่นคนอื่น
        for (PlayerData player : otherPlayers) {
            g.drawImage(character[player.getCharacterIndex()], player.getX(), player.getY(), this);
            g.drawString(player.getName(), player.getX(), player.getY() - 10);
        }
        

        // วาดตัวละครที่เลือกตามเงื่อนไขของผู้เล่นหลัก
        if ("c01".equals(ingame.textcha)) {
            g.drawImage(character[0], x, y, 700, 500, this);
        } else if ("c02".equals(ingame.textcha)) {
            g.drawImage(character[1], x, y, 700, 500, this);
        } else if ("c03".equals(ingame.textcha)) {
            g.drawImage(character[2], x, y, 700, 500, this);
        } else if ("c04".equals(ingame.textcha)) {
            g.drawImage(character[3], x, y, 700, 500, this);
        } else if ("c05".equals(ingame.textcha)) {
            g.drawImage(character[4], x, y, 700, 500, this);
        }

        g.drawString(ingame.name, nameX, nameY); // วาดชื่อผู้เล่นหลัก
    }
}
