import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;

public class about2 extends JFrame {

    panalAbout2 pAbout2 = new panalAbout2(this);

    public about2() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(pAbout2);
    }

}

class panalAbout2 extends JPanel {

    Image imageBgk = Toolkit.getDefaultToolkit().getImage("C:/oopProjactGram/imgAbout/BKG.png");

    JButton buttonMenu;
    JButton buttonBack;

    about2 abt2;

    panalAbout2(about2 abt2) {

        this.abt2 = abt2;

        ImageIcon iconMenu = new ImageIcon("C:/oopProjactGram/imgAbout/bb1.png");
        ImageIcon iconBack = new ImageIcon("C:/oopProjactGram/imgAbout/bb3.png");

        // ปรับขนาดของรูปภาพให้เหมาะกับปุ่ม
        Image imgMenu = iconMenu.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        Image imgBack = iconBack.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);

        iconMenu = new ImageIcon(imgMenu);
        iconBack = new ImageIcon(imgBack);

        // สร้างปุ่มพร้อมรูปภาพแทนข้อความ
        buttonMenu = new JButton(iconMenu);
        buttonMenu.setBounds(0, 0, 100, 50);

        buttonBack = new JButton(iconBack);
        buttonBack.setBounds(180, 680, 100, 50);

        // ปิด layout manager เพื่อสามารถตั้งตำแหน่งเองได้
        setLayout(null);

        // เพิ่มปุ่มทั้งสองลงใน JPanel
        add(buttonMenu);
        add(buttonBack);

        buttonMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menugame MenuGame1 = new menugame();
                MenuGame1.setVisible(true);
                abt2.dispose();
            }

        });

        buttonBack.addActionListener(new ActionListener() {

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

}
