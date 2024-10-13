import java.awt.Graphics; // นำเข้าคลาส Graphics
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class itselfgame extends JFrame {
    String textcha;
    PanelGame pInGame;

    itselfgame(String textcha) {
        this.textcha = textcha;
        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pInGame = new PanelGame(this); // สร้าง PanelGame ที่นี่
        add(pInGame);
    }

}

class PanelGame extends JPanel {
    itselfgame ingame;
    Image[] character = new Image[5];
    Image bg;

    PanelGame(itselfgame ingame) {
        this.ingame = ingame;
        bg = Toolkit.getDefaultToolkit().getImage("E:/gamefinal/ingame/bgingmae.png");
        for (int i = 0; i < 5; i++) {
            character[i] = new ImageIcon("E:/gamefinal/imgip/" + (i + 1) + ".png").getImage();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        if ("c01".equals(ingame.textcha)) {
            g.drawImage(character[0], 100, 200, 700, 500, this);
        } else if ("c02".equals(ingame.textcha)) {
            g.drawImage(character[1], 100, 200, 700, 500, this);
        } else if ("c03".equals(ingame.textcha)) {
            g.drawImage(character[2], 100, 200, 700, 500, this);
        } else if ("c04".equals(ingame.textcha)) {
            g.drawImage(character[3], 100, 200, 700, 500, this);
        } else if ("c05".equals(ingame.textcha)) {
            g.drawImage(character[4], 100, 200, 700, 500, this);
        }
    }
}
