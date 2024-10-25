import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WaitPlayer extends JFrame {
    private String playerName;
    private String playerIP;
    private String character;
    private JButton startButton; // ปุ่มสำหรับเริ่มเกม

    public WaitPlayer(String playerName, String playerIP, String character) {
        this.playerName = playerName;
        this.playerIP = playerIP;
        this.character = character;

        Myjf mm = new Myjf();
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(mm.panal, BorderLayout.CENTER);

        // เพิ่มปุ่ม Start Game
        startButton = new JButton("Start Game");
        startButton.setBounds(650, 600, 140, 50);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // เรียกใช้ startGame() เมื่อกดปุ่ม
            }
        });
        mm.panal.add(startButton);
        mm.panal.setLayout(null); // ตั้ง layout เป็น null เพื่อจัดตำแหน่งปุ่ม
    }

    private void startGame() {
        // เปิด GameClient เมื่อกดปุ่ม Start Game
        GameClient ingame = new GameClient(character, playerName, playerIP);
        ingame.setVisible(true);
        dispose(); // ปิดหน้ารอ WaitPlayer
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
    Image wallpaper, loading;

    Mypn() {
        setLayout(null);
        wallpaper = Toolkit.getDefaultToolkit().getImage("D:/WEEK_SERVER/wait.png");
       
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(wallpaper, 0, 0, this);
      
    }
}
