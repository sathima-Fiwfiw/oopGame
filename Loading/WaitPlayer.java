import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WaitPlayer {
    public static void main(String[] args) {
        Myjf mm=new Myjf();
        mm.setVisible(true);
    }
    
}

class Myjf extends JFrame{

    Mypn panal=new Mypn();


    Myjf(){

        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(panal,BorderLayout.CENTER);
  
    }
}

class Mypn extends JPanel{

    Image wallpaper,loading;

    Mypn(){

        setLayout(null);

        wallpaper = Toolkit.getDefaultToolkit().getImage("D:/final_oopgame/waiting.png");
        loading = Toolkit.getDefaultToolkit().getImage("D:/final_oopgame/loading.gif");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(wallpaper, 0, 0, this);
        g.drawImage(loading, 565, 635, this);
    }
}
