import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class menugame extends JFrame {
    menugame() {
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        menugame menu = new menugame();
        panelMenu panel = new panelMenu(menu);
        menu.add(panel);
        menu.setVisible(true);
    }
}

class panelMenu extends JPanel {
    Image bg, candyblue, candyorenge, donut;
    JButton start, about, exit;
    menugame menu;

    panelMenu(menugame menu) {
        this.menu = menu;

        bg = Toolkit.getDefaultToolkit().getImage("E:/gamefinal/imgmenu/bgMenu1.png");
        candyblue = Toolkit.getDefaultToolkit().getImage("E:/gamefinal/imgmenu/candyBlue.png");
        candyorenge = Toolkit.getDefaultToolkit().getImage("E:/gamefinal/imgmenu/candyorenge.png");
        donut = Toolkit.getDefaultToolkit().getImage("E:/gamefinal/imgmenu/donut.png");

        ImageIcon startIcon = new ImageIcon("E:/gamefinal/imgmenu/strat.png");
        ImageIcon aboutIcon = new ImageIcon("E:/gamefinal/imgmenu/about.png");
        ImageIcon exitIcon = new ImageIcon("E:/gamefinal/imgmenu/exit.png");

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
                about.setVisible(true);
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

    }

}
