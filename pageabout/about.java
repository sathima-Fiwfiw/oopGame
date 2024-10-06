import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;

class about extends JFrame {

    panalAbout pAbout = new panalAbout(this);

    public about() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(pAbout);
    }

}

class panalAbout extends JPanel {

    Image imageBg = Toolkit.getDefaultToolkit().getImage("C:/oopProjactGram/imgAbout/getCandy.png");
    JButton buttonMenu1;
    JButton buttonNext;
    about abt;

    panalAbout(about abt) {

        ImageIcon iconMenu1 = new ImageIcon("C:/oopProjactGram/imgAbout/bb1.png");
        ImageIcon iconNext = new ImageIcon("C:/oopProjactGram/imgAbout/bb2.png");

        this.abt = abt;

        // ปรับขนาดของรูปภาพให้เหมาะกับปุ่ม
        Image imgMenu1 = iconMenu1.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);
        Image imgNext = iconNext.getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH);

        iconMenu1 = new ImageIcon(imgMenu1);
        iconNext = new ImageIcon(imgNext);

        // สร้างปุ่มพร้อมรูปภาพแทนข้อความ
        buttonMenu1 = new JButton(iconMenu1);
        buttonMenu1.setBounds(0, 0, 100, 50);

        buttonNext = new JButton(iconNext);
        buttonNext.setBounds(1140, 680, 100, 50);

        // ปิด layout manager เพื่อสามารถตั้งตำแหน่งเองได้
        setLayout(null);

        // เพิ่มปุ่มทั้งสองลงใน JPanel
        add(buttonMenu1);
        add(buttonNext);

        buttonMenu1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menugame MenuGame = new menugame();
                MenuGame.setVisible(true);
                abt.dispose();
            }

        });

        buttonNext.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                about2 About2 = new about2();
                About2.setVisible(true);
                abt.dispose();
            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(imageBg, 0, 0, getWidth(), getHeight(), this);// วาดพื้นหลัง

    }

}
