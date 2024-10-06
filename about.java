import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseMotionListener;
import java.awt.Graphics;
import java.io.File;

public class about extends JFrame {

    public about() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        about abt = new about();
        panalAbout pAbout = new panalAbout();
        abt.add(pAbout);
        abt.setVisible(true);
    }
}

class panalAbout extends JPanel {

    Image imageBg = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + File.separator + "getCandy.png");

    JButton button1;
    JButton button2;

    panalAbout() {

        ImageIcon icon1 = new ImageIcon(System.getProperty("user.dir") + File.separator + "bb1.png");
        ImageIcon icon2 = new ImageIcon(System.getProperty("user.dir") + File.separator + "bb2.png");

        // ปรับขนาดของรูปภาพให้เหมาะกับปุ่ม
        Image img1 = icon1.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        Image img2 = icon2.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);

        icon1 = new ImageIcon(img1);
        icon2 = new ImageIcon(img2);

        // สร้างปุ่มพร้อมรูปภาพแทนข้อความ
        button1 = new JButton(icon1);
        button1.setBounds(0, 0, 100, 50);

        button2 = new JButton(icon2);
        button2.setBounds(1140, 680, 100, 50);

        // ปิด layout manager เพื่อสามารถตั้งตำแหน่งเองได้
        setLayout(null);

        // เพิ่มปุ่มทั้งสองลงใน JPanel
        add(button1);
        add(button2);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(imageBg, 0, 0, getWidth(), getHeight(), this);// วาดพื้นหลัง

    }

    public static void main(String[] args) {
    }

    
}
