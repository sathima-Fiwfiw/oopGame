import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;



class Ipgame extends JFrame {

    CustomPanel jPanel = new CustomPanel(this);

    Ipgame() {
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
    JButton back;
    Ipgame ipgame;

    CustomPanel(Ipgame ipgame) {
        this.ipgame = ipgame;
        setSize(1440, 810);
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imagecharacter" + File.separator + "bg.png");

        // Configure text fields
        textname.setBounds(275, 55, 350, 70); 
        textip.setBounds(980, 55, 350, 70); 
        textip.setBackground(new Color(255,212,131));
        textname.setBackground(new Color(255,212,131));
        textname.setFont(new Font("Arial Black", Font.PLAIN, 30));
        textip.setFont(new Font("Arial Black", Font.PLAIN, 30));
        add(textname);
        add(textip);

            for (int i = 0; i < buttons.length; i++) {
                
                ImageIcon character = new ImageIcon(System.getProperty("user.dir") + 
                                File.separator +"imagecharacter"+ File.separator + (i + 1) + ".png"); 
                Image scaledImage = character.getImage().getScaledInstance(1300, 800, Image.SCALE_SMOOTH); // Adjust the size here
                buttons[i] = new JButton(new ImageIcon(scaledImage)); 
                buttons[i].setBounds(75 + (i * 260), 280, 220, 320); 
                
    
                buttons[i].setContentAreaFilled(false);
                buttons[i].setBorderPainted(false); 
                add(buttons[i]);
            }

            ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + 
            File.separator + "imagecharacter" + File.separator + "enter.png"); 
            Image icoImage = icon.getImage().getScaledInstance(1180, 700,Image.SCALE_SMOOTH);
            enter =new JButton(new ImageIcon(icoImage));
            enter.setBounds(600, 630, 150, 90);
            add(enter);

            ImageIcon icon2 = new ImageIcon(System.getProperty("user.dir") + 
            File.separator + "imagecharacter" + File.separator + "back.png"); 
            Image icoImage2 = icon2.getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH);
            back =new JButton(new ImageIcon(icoImage2));
            back.setBounds(0, 700, 90, 60);
            add(back);

            back.addActionListener(new ActionListener() {

             
             
                @Override
                public void actionPerformed(ActionEvent e) {
                    menugame main = new menugame();
                    main.setVisible(true);
                    ipgame.dispose();
                
    }});
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
    }
}
