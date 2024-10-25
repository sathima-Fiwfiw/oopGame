import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitPlayer extends JFrame {
    private String playerName;
    private String playerIP;
    private String character;
    private JButton startButton; // ปุ่มสำหรับเริ่มเกม
    
    public WaitPlayer(String playerName, String playerIP, String character) {
        this.playerName = playerName;
        this.playerIP = playerIP;
        this.character = character;

        // สร้างหน้าต่างรอด้วยภาพพื้นหลัง
        Myjf mm = new Myjf();
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(mm.panal, BorderLayout.CENTER);

        // เพิ่มปุ่ม Start Game
        ImageIcon start = new ImageIcon(System.getProperty("user.dir") + 
            File.separator + "imgloadwait" + File.separator + "bttstart.png"); 
        Image startButtonImage = start.getImage().getScaledInstance(138, 58,Image.SCALE_SMOOTH);

        startButton = new JButton(new ImageIcon(startButtonImage));
        startButton.setBounds(650, 670, 140, 60);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //startGame(); // เรียกใช้ startGame() เมื่อกดปุ่ม
            }
        });
        mm.panal.add(startButton);
        mm.panal.setLayout(null); // ตั้ง layout เป็น null เพื่อจัดตำแหน่งปุ่ม
        
        setVisible(true); // แสดงหน้าต่าง
    }

    private void startGame() {
        // เปิด GameClient เมื่อกดปุ่ม Start Game
        GameClient ingame = new GameClient(character, playerName, playerIP);
        ingame.setVisible(true);
        dispose(); // ปิดหน้ารอ WaitPlayer
    }
    
    public static void main(String[] args) {
        // ทดสอบการแสดงหน้ารอ
        new WaitPlayer("Player1", "127.0.0.1", "Character1");
    }
}

class Myjf extends JFrame {
    Mypn panal = new Mypn();

    Myjf() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(panal, BorderLayout.CENTER);
    }
}

class Mypn extends JPanel {
    Image wallpaper, decorate1, decorate2, decorate3, decorate4, decorate5, decorate6;

    Mypn() {
        setLayout(null);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดภาพพื้นหลังและตกแต่ง
        g.drawImage(wallpaper, 0, 0, this);
        g.drawImage(decorate1, 390, 550, 300, 200, this);
        g.drawImage(decorate2, 1050, -70, 550, 300, this);
        g.drawImage(decorate3, 55, 664, 250, 150, this);
        g.drawImage(decorate4, 300, -35, 300, 200, this);
        g.drawImage(decorate5, 770, -25, 350, 170, this);
        g.drawImage(decorate6, -50, 20, 400, 250, this);
    }
}
