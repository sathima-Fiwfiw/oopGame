import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.ImageIcon;

public class Ipgame {

    public static void main(String[] args) {
        Myframe ff = new Myframe();
        ff.setVisible(true);
    }
}

class Myframe extends JFrame {

    CustomPanel jPanel = new CustomPanel();

    Myframe() {
        setSize(1440, 810);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        jPanel.setLayout(null);
        add(jPanel, BorderLayout.CENTER);
    }
}

class CustomPanel extends JPanel {
    Image bg;
    JTextField textname = new JTextField();
    JTextField textip = new JTextField();
    JButton[] buttons = new JButton[5]; // Array to hold 5 buttons
    JButton enter;

    CustomPanel() {
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imagecharacter" + File.separator + "bg.png");

        // Configure text fields
        textname.setBounds(255, 55, 350, 70); 
        textip.setBounds(935, 55, 350, 70); 
        textname.setFont(new Font("monbait", Font.PLAIN, 30));
        textip.setFont(new Font("Monospaced", Font.PLAIN, 30));
        add(textname);
        add(textip);

            for (int i = 0; i < buttons.length; i++) {
                
                ImageIcon character = new ImageIcon(System.getProperty("user.dir") + 
                                File.separator +"imagecharacter"+ File.separator + (i + 1) + ".png"); 
                Image scaledImage = character.getImage().getScaledInstance(1300, 800, Image.SCALE_SMOOTH); // Adjust the size here
                buttons[i] = new JButton(new ImageIcon(scaledImage)); 
                buttons[i].setBounds(70 + (i * 250), 280, 220, 320); 
                
    
                buttons[i].setContentAreaFilled(false); 
                buttons[i].setBorderPainted(false); 
                add(buttons[i]);
            }

            ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + 
            File.separator + "imagecharacter" + File.separator + "enter.png"); 
            Image icoImage = icon.getImage().getScaledInstance(1280, 700, Image.SCALE_SMOOTH);
            enter =new JButton(new ImageIcon(icoImage));
            enter.setBounds(60, 630, 150, 90);
            add(enter);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
