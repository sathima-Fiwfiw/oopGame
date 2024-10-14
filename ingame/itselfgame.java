import java.awt.Graphics; // นำเข้าคลาส Graphics
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.Timer; 
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class itselfgame extends JFrame {
    String textcha;
    PanelGame pInGame;

    itselfgame(String textcha) {
        this.textcha = textcha;
        setSize(1450, 840);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pInGame = new PanelGame(this); // สร้าง PanelGame ที่นี่
        pInGame.addMouseMotionListener(pInGame);
        pInGame.addMouseListener(pInGame);
        add(pInGame);
    }

}

class PanelGame extends JPanel implements MouseMotionListener,MouseListener {
    itselfgame ingame;
    Image[] character = new Image[5];
    Image bg,hand;
    int movecharacter = 0, characterY = 540;
    int characterHeigth = 250;
    int c01Width = 200, c02Width = 150, c03Width = 150, c04Width = 120, c05Width = 160;
    boolean isJumping = false;
    boolean ishand = true;
    Timer jumpTimer;
    Timer handTimer;
    int jumpHeight = 10;
    boolean jumpingUp = true; // ตัวแปรเช็คทิศทางกระโดด
    int handX = 0, handY = 710; // ตำแหน่งของมือ
    int handWidth = 80, handHeight = 100; 
    Random random = new Random();
   

    PanelGame(itselfgame ingame) {
        this.ingame = ingame;
        bg = Toolkit.getDefaultToolkit().getImage("C:/oopGame/ingame/bgingame.png");
        hand = Toolkit.getDefaultToolkit().getImage("C:/oopGame/ingame/handgrost.png");
        for (int i = 0; i < 5; i++) {
            character[i] = new ImageIcon("C:/oopGame/imageip/" + (i + 1) + ".png").getImage(); 
        }

        handX = random.nextInt(1100) + 10; 
        jumpTimer = new Timer(20, new ActionListener() {
        
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jumpingUp) {
                    characterY -= 10; // กระโดดขึ้น
                    if (characterY <= 400) { // ถึงจุดสูงสุดแล้ว
                        jumpingUp = false; // เปลี่ยนทิศทางไปลง
                    }
                } else {
                    characterY += 10; // ลง
                    if (characterY >= 540) { // ถึงพื้นแล้ว
                        characterY = 540; // รีเซ็ตตำแหน่ง
                        isJumping = false; // รีเซ็ต flag ว่ากระโดดเสร็จแล้ว
                        jumpTimer.stop(); // หยุด timer
                    }
                }
                repaint(); // วาดใหม่ทุกครั้ง
            }
        });

        handTimer = new Timer(3000, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                ishand = !ishand; // สลับสถานะการแสดงมือ
                if (ishand) {
                    handX = random.nextInt(1100) + 10; // กำหนดตำแหน่งมือใหม่แบบสุ่ม
                }
                repaint(); // วาดใหม่ทุกครั้ง
            }
        });
        handTimer.start(); // เริ่ม timer ของมือ

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        if ("c01".equals(ingame.textcha)) {
            g.drawImage(character[0], movecharacter-100, characterY, c01Width, characterHeigth, this);
        } else if ("c02".equals(ingame.textcha)) {
            g.drawImage(character[1], movecharacter-112, characterY, c02Width, characterHeigth, this);
        } else if ("c03".equals(ingame.textcha)) {
            g.drawImage(character[2], movecharacter-89, characterY, c03Width, characterHeigth, this);
        } else if ("c04".equals(ingame.textcha)) {
            g.drawImage(character[3], movecharacter-90, characterY, c04Width, characterHeigth, this);
        } else if ("c05".equals(ingame.textcha)) {
            g.drawImage(character[4], movecharacter-125, characterY, c05Width, characterHeigth, this);
        }
        if(ishand){
            g.drawImage(hand, handX , handY, handWidth, handHeight, this);
        }
        
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        movecharacter = e.getX();
        checkCollision();
        

        // ตัวละคร c01 - เช็คขอบ
        if ("c01".equals(ingame.textcha)) {
            if (movecharacter - 100 < 0) { // ขอบซ้าย
                movecharacter = 100;
            }
            if (movecharacter + 90 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 90;
            }
        }
        // ตัวละคร c02 - เช็คขอบ
        else if ("c02".equals(ingame.textcha)) {
            if (movecharacter - 110 < 0) { // ขอบซ้าย
                movecharacter = 110;
            }
            if (movecharacter + 40 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 40;
            }
        }
        // ตัวละคร c03 - เช็คขอบ
        else if ("c03".equals(ingame.textcha)) {
            if (movecharacter - 90 < 0) { // ขอบซ้าย
                movecharacter = 90;
            }
            if (movecharacter + 65 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 65;
            }
        }
        // ตัวละคร c04 - เช็คขอบ
        else if ("c04".equals(ingame.textcha)) {
            if (movecharacter - 95 < 0) { // ขอบซ้าย
                movecharacter = 95;
            }
            if (movecharacter + 30 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 30;
            }
        }
        // ตัวละคร c05 - เช็คขอบ
        else if ("c05".equals(ingame.textcha)) {
            if (movecharacter - 125 < 0) { // ขอบซ้าย
                movecharacter = 125;
            }
            if (movecharacter + 40 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 40;
            }
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isJumping) { // ตรวจสอบว่ากำลังไม่กระโดดอยู่
            isJumping = true; // ตั้งค่าให้กำลังกระโดด
            jumpingUp = true;
            characterY = 540; // เริ่มที่พื้น
            jumpTimer.start(); // เริ่ม timer
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    void checkCollision() {
        Timer bounce;
        int characterLeft = movecharacter; //ซ้าย
        int characterRight = 0; //ขวา
        int characterBottom = characterY + characterHeigth; //ล่าง

        if ("c01".equals(ingame.textcha)) {
           characterRight = movecharacter + c01Width;
        } else if ("c02".equals(ingame.textcha)) {
           characterRight = movecharacter + c02Width;
        } else if ("c03".equals(ingame.textcha)) {
            characterRight = movecharacter + c03Width;
        } else if ("c04".equals(ingame.textcha)) {
           characterRight = movecharacter + c04Width;
        } else if ("c05".equals(ingame.textcha)) {
           characterRight = movecharacter + c05Width;
        }
        bounce = new Timer(50, new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                characterY = 540;
                repaint(); // วาดใหม่ทุกครั้ง
            }
        });
       
        
    
        // กำหนดขอบเขตของมือ
        int handLeft = handX; // ขอบซ้ายของมือ
        int handRight = handX + handWidth + 100; // ขอบขวาของมือ 
        int handTop = handY; // ขอบบนของมือ

        if (ishand) {
            if (characterRight > handLeft && characterLeft < handRight && characterBottom > handTop) {
                // กรณีชนจากซ้ายไปขวา
                if (characterLeft < handRight) {
                    movecharacter += 100; // เด้งไปทางขวา
                    characterY -= 50;
                    bounce.start();
                }
                // กรณีชนจากขวาไปซ้าย
                else if (characterRight > handLeft) {
                    movecharacter -= 100; // เด้งไปทางซ้าย
                    characterY -= 50;
                    bounce.start();
                }
                // กรณีชนจากด้านล่าง
                if (characterBottom > handTop) {
                    characterY = 450; // ปรับตำแหน่งแนวตั้ง
                    bounce.start();
                }
                repaint(); // วาดใหม่
            }
        }
    }
    
 
}
