import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class menugame extends JFrame {
    panelMenu panel = new panelMenu(this);

    menugame() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
    }

}

class panelMenu extends JPanel {
    Image bg, candyblue, candyorenge, donut ,candy;
    JButton start, about, exit;
    menugame menu;

    panelMenu(menugame menu) {
        this.menu = menu;

        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imgmenu" + File.separator + "bgMenu1.png");

        candyblue = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imgmenu" + File.separator + "candyBlue.png");
        candyorenge  = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imgmenu" + File.separator + "candyorenge.png");
        donut =  Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imgmenu" + File.separator + "donut.png");

      /*   candy =  Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imageRain" + File.separator + "11.png");*/

        ImageIcon startIcon = new ImageIcon(System.getProperty("user.dir") + 
        File.separator +"imgmenu"+ File.separator + "strat.png");

        ImageIcon aboutIcon = new ImageIcon(System.getProperty("user.dir") + 
        File.separator +"imgmenu"+ File.separator + "about.png");

       ImageIcon exitIcon = new ImageIcon(System.getProperty("user.dir") + 
        File.separator +"imgmenu"+ File.separator + "exit.png");

        Image startImg = startIcon.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH);
        Image aboutImg = aboutIcon.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH);
        Image exitImg = exitIcon.getImage().getScaledInstance(300, 80, Image.SCALE_SMOOTH);

        start = new JButton(new ImageIcon(startImg));
        about = new JButton(new ImageIcon(aboutImg));
        exit = new JButton(new ImageIcon(exitImg));

        setLayout(null);
        start.setBounds(880, 350, 300, 80);
        about.setBounds(880, 460, 300, 80);
        exit.setBounds(880, 570, 300, 80);

        start.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Ipgame ipgame = new Ipgame();
                ipgame.setVisible(true);
                menu.dispose();
            }

        });

        about.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                about aboutgame = new about();
                aboutgame.setVisible(true);
                menu.dispose();
            }

        });

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });

        add(start);
        add(about);
        add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(candyblue, 840, 298, this);
        g.drawImage(candyorenge, 1117, 375, this);
        g.drawImage(donut, 790, 490, this);
       //g.drawImage(candy, 0, 0,60,35, this);

    }

}
