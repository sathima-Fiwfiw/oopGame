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
    int movecharacter = 0, jumpcharacter = 550;
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

        handX = random.nextInt(1100) + 100; 
        jumpTimer = new Timer(20, new ActionListener() {
        
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jumpingUp) {
                    jumpcharacter -= 10; // กระโดดขึ้น
                    if (jumpcharacter <= 400) { // ถึงจุดสูงสุดแล้ว
                        jumpingUp = false; // เปลี่ยนทิศทางไปลง
                    }
                } else {
                    jumpcharacter += 10; // ลง
                    if (jumpcharacter >= 550) { // ถึงพื้นแล้ว
                        jumpcharacter = 550; // รีเซ็ตตำแหน่ง
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
                    handX = random.nextInt(1100) + 100; // กำหนดตำแหน่งมือใหม่แบบสุ่ม
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
            g.drawImage(character[0], movecharacter-160, jumpcharacter, 200, 250, this);
        } else if ("c02".equals(ingame.textcha)) {
            g.drawImage(character[1], movecharacter-112, jumpcharacter, 200, 250, this);
        } else if ("c03".equals(ingame.textcha)) {
            g.drawImage(character[2], movecharacter-89, jumpcharacter, 200, 250, this);
        } else if ("c04".equals(ingame.textcha)) {
            g.drawImage(character[3], movecharacter-96, jumpcharacter, 200, 250, this);
        } else if ("c05".equals(ingame.textcha)) {
            g.drawImage(character[4], movecharacter-125, jumpcharacter, 200, 250, this);
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
       // checkCollision();
        

        // ตัวละคร c01 - เช็คขอบ
        if ("c01".equals(ingame.textcha)) {
            if (movecharacter - 155 < 0) { // ขอบซ้าย
                movecharacter = 155;
            }
            if (movecharacter + 40 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 40;
            }
        }
        // ตัวละคร c02 - เช็คขอบ
        else if ("c02".equals(ingame.textcha)) {
            if (movecharacter - 110 < 0) { // ขอบซ้าย
                movecharacter = 110;
            }
            if (movecharacter + 90 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 90;
            }
        }
        // ตัวละคร c03 - เช็คขอบ
        else if ("c03".equals(ingame.textcha)) {
            if (movecharacter - 90 < 0) { // ขอบซ้าย
                movecharacter = 90;
            }
            if (movecharacter + 105 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 105;
            }
        }
        // ตัวละคร c04 - เช็คขอบ
        else if ("c04".equals(ingame.textcha)) {
            if (movecharacter - 95 < 0) { // ขอบซ้าย
                movecharacter = 95;
            }
            if (movecharacter + 100 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 100;
            }
        }
        // ตัวละคร c05 - เช็คขอบ
        else if ("c05".equals(ingame.textcha)) {
            if (movecharacter - 115 < 0) { // ขอบซ้าย
                movecharacter = 115;
            }
            if (movecharacter + 70 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 70;
            }
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isJumping) { // ตรวจสอบว่ากำลังไม่กระโดดอยู่
            isJumping = true; // ตั้งค่าให้กำลังกระโดด
            jumpingUp = true;
            jumpcharacter = 550; // เริ่มที่พื้น
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
        // ปรับขนาดขอบเขตของตัวละครให้ตรงกับขนาดจริงที่มองเห็น
        int characterVisibleWidth = 500; // สมมติว่าตัวละครจริงกินพื้นที่แค่ 500px ในภาพ 960px
        int characterVisibleHeight = 400; // สมมติว่าตัวละครจริงกินพื้นที่ 400px ในภาพ 540px
        int offsetX = (960 - characterVisibleWidth) / 2; // หาระยะขอบว่างซ้าย-ขวาในภาพ 960px
        int offsetY = (540 - characterVisibleHeight) / 2; // หาระยะขอบว่างบน-ล่างในภาพ 540px
    
        int characterLeft = movecharacter - offsetX; // ขอบซ้ายของตัวละคร
        int characterRight = characterLeft + characterVisibleWidth - 200; // ขอบขวาของตัวละคร
        int characterTop = jumpcharacter - offsetY; // ขอบบนของตัวละคร
        int characterBottom = characterTop + characterVisibleHeight; // ขอบล่างของตัวละคร
    
        // กำหนดขอบเขตของมือ
        int handLeft = handX; // ขอบซ้ายของมือ
        int handRight = handLeft + 165; // ขอบขวาของมือ (ตามขนาดจริง 165)
        int handTop = handY; // ขอบบนของมือ
        int handBottom = handTop + 222; // ขอบล่างของมือ (ตามขนาดจริง 222)
    
        // ตรวจสอบการชน
        if (characterRight > handLeft && characterLeft < handRight && characterBottom > handTop && characterTop < handBottom) {
            // ชนแล้วกระเด้งไปตามทิศทาง
            if (movecharacter > handLeft) {
                movecharacter += 100;
            } else {
                movecharacter -= 100;
            }
            jumpcharacter = 440; // เด้งตัวขึ้น
            repaint();
        }
    }
    
 
}
