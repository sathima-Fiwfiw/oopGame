import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class character extends JFrame {
    character() {
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        character Fchr = new character();
        PanelCharacter pchr = new PanelCharacter(Fchr);
        Fchr.add(pchr);
        Fchr.setVisible(true);

    }
}

class PanelCharacter extends JPanel {
    JButton enter;
    JTextField textcha;
    String text = " ";
    character Fchr;

    PanelCharacter(character Fchr) {
        this.Fchr = Fchr;
        setSize(900, 700);
        setLayout(null);

        enter = new JButton("ENTER");
        enter.setBounds(400, 30, 100, 30);
        textcha = new JTextField();
        textcha.setBounds(50, 20, 300, 50);

        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                text = textcha.getText();
                itselfgame inGame = new itselfgame(text);
                inGame.setVisible(true);
                Fchr.dispose();
                printText();
                repaint();
            }
        });

        add(textcha);
        add(enter);
    }

    // เมธอดสำหรับพิมพ์ค่า
    private void printText() {
        System.out.println("Text entered: " + text); // พิมพ์ค่าที่เก็บไว้
    }

}
