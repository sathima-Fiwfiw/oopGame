import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.io.File;

public class about2 extends JFrame {

    public about2() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        about abt2 = new about();
        panalAbout2 pAbout2 = new panalAbout2(abt2);
        abt2.add(pAbout2);
        abt2.setVisible(true);
    }
}

class panalAbout2 extends JPanel {

    Image imageBgk = Toolkit.getDefaultToolkit()
            .createImage(System.getProperty("user.dir") + File.separator + "BKG.png");

    JButton button3;
    JButton button4;

    about abt2 ;

    panalAbout2(about abt2 ) {

        ImageIcon icon3 = new ImageIcon(System.getProperty("user.dir") + File.separator + "bb1.png");
        ImageIcon icon4 = new ImageIcon(System.getProperty("user.dir") + File.separator + "bb3.png");

        // ปรับขนาดของรูปภาพให้เหมาะกับปุ่ม
        Image img3 = icon3.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        Image img4 = icon4.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);

        icon3 = new ImageIcon(img3);
        icon4 = new ImageIcon(img4);

        // สร้างปุ่มพร้อมรูปภาพแทนข้อความ
        button3 = new JButton(icon3);
        button3.setBounds(0, 0, 100, 50);

        button4 = new JButton(icon4);
        button4.setBounds(180, 680, 100, 50);

        // ปิด layout manager เพื่อสามารถตั้งตำแหน่งเองได้
        setLayout(null);

        // เพิ่มปุ่มทั้งสองลงใน JPanel
        add(button3);
        add(button4);

        button3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menugame MenuGame1 = new menugame();
                MenuGame1.setVisible(true);
                abt2.dispose();
            }
            
        });

        button4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                about about1 = new about();
                about1.setVisible(true);
                abt2.dispose();
            }
            
        });

        

       
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(imageBgk, 0, 0, getWidth(), getHeight(), this);// วาดพื้นหลัง

    }

    public static void main(String[] args) {
    }

    
}
