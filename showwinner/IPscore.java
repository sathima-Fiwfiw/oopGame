import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Graphics;

public class IPscore extends JFrame {
    String scoreON;
    String nameUser;
    String characterID;
    String scoreLOWER;

    IPscore(String scoreON, String nameUser, String characterID, String scoreLOWER) {

        setSize(1440, 810);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.scoreON = scoreON;
        this.nameUser = nameUser;
        this.characterID = characterID;
        this.scoreLOWER = scoreLOWER;
    }

    public static void main(String[] args) {
        IPscore Fscore = new IPscore("1000", "Pear", "c05", "1000");
        scorePanel scorepanel = new scorePanel(Fscore);
        Fscore.add(scorepanel);
        Fscore.setVisible(true);
    }
}

//
//
//
class scorePanel extends JPanel {
    Image imageScore = Toolkit.getDefaultToolkit().getImage("C:/oopProjactGram/ScoreImg/ImgScore.png");
    JButton ScoreMenu;
    Image[] character = new Image[5];

    int characterHeight = 130;
    int c01Width = 100, c02Width = 80, c03Width = 85, c04Width = 87, c05Width = 80;

    IPscore Fscore;

    scorePanel(IPscore Fscore) {

        this.Fscore = Fscore;
        setLayout(null); // ใช้ null layout เพื่อจัดตำแหน่งปุ่มเอง

        ImageIcon iconScore = new ImageIcon("C:/oopProjactGram/ScoreImg/buttonScore.png");
        Image imhScore = iconScore.getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH);
        iconScore = new ImageIcon(imhScore);

        ScoreMenu = new JButton(iconScore);
        ScoreMenu.setBounds(650, 550, 150, 50); // ตั้งตำแหน่งปุ่ม
        add(ScoreMenu);

        // แสดงคะแนน
        JLabel scoreON = new JLabel(" " + Fscore.scoreON);
        scoreON.setBounds(1150, 20, 200, 50);
        scoreON.setFont(new Font("Gigi", Font.BOLD, 50));
        add(scoreON);

        // แสดงชื่อผู้ใช้
        JLabel nameUser = new JLabel("" + Fscore.nameUser);
        nameUser.setBounds(760, 300, 200, 50);
        nameUser.setFont(new Font("Centaur", Font.BOLD, 50));
        add(nameUser);

        // เวลา
        JLabel time = new JLabel("00.00");
        time.setBounds(300, 15, 200, 50);
        time.setFont(new Font("Gigi", Font.BOLD, 50));
        add(time);

        JLabel scoreLOWER = new JLabel("" + Fscore.scoreLOWER);
        scoreLOWER.setBounds(760, 415, 200, 50);
        scoreLOWER.setFont(new Font("Gigi", Font.BOLD, 50));
        add(scoreLOWER);

        // โหลดรูปตัวละคร
        for (int i = 0; i < character.length; i++) {

            character[i] = new ImageIcon("C:/oopProjactGram/GhostCharacter/" + (i + 1) + ".png").getImage();
        }

        ScoreMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menugame menugameScore = new menugame();
                menugameScore.setVisible(true);
                Fscore.dispose();
            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดภาพพื้นหลัง
        g.drawImage(imageScore, 0, 0, getWidth(), getHeight(), this);

        // วาดตัวละครที่เลือก
        Image currentCharacter = getCurrentCharacter();
        g.drawImage(currentCharacter, 540, 250, getCharacterWidth(), characterHeight, this);
    }

    // ฟังก์ชันสำหรับเลือกตัวละครตาม characterID
    private Image getCurrentCharacter() {
        switch (Fscore.characterID) {
            case "c01":
                return character[0];
            case "c02":
                return character[1];
            case "c03":
                return character[2];
            case "c04":
                return character[3];
            case "c05":
                return character[4];
            default:
                return null;
        }
    }

    private int getCharacterWidth() {
        switch (Fscore.characterID) {
            case "c01":
                return c01Width;
            case "c02":
                return c02Width;
            case "c03":
                return c03Width;
            case "c04":
                return c04Width;
            case "c05":
                return c05Width;
            default:
                return 0;
        }
    }

}