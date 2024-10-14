import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class itselfgame extends JFrame {
    String textcha;
    PanelGame pInGame;

    itselfgame(String textcha) {
        this.textcha = textcha;
        setSize(1450, 840);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pInGame = new PanelGame(this);
        pInGame.addMouseMotionListener(pInGame);
        pInGame.addMouseListener(pInGame);
        add(pInGame);
    }

    public static void main(String[] args) {
        new itselfgame("c02").setVisible(true);
    }
}

class PanelGame extends JPanel implements MouseMotionListener, MouseListener {
    itselfgame ingame;
    Image[] character = new Image[5];
    Image bg, hand;
    int movecharacter = 0, characterY = 540;
    int characterHeight = 250;
    int c01Width = 200, c02Width = 150, c03Width = 150, c04Width = 120, c05Width = 160;
    boolean isJumping = false, ishand = true;
    Timer actionTimer;
    int handX = 0, handY = 710;
    int handWidth = 80, handHeight = 100;
    Random random = new Random();
    private Robot robot;

    PanelGame(itselfgame ingame) {
        this.ingame = ingame;
        bg = Toolkit.getDefaultToolkit().getImage("C:/oopGame/ingame/bgingame.png");
        hand = Toolkit.getDefaultToolkit().getImage("C:/oopGame/ingame/handgrost.png");

        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/oopGame/imageip/" + (i + 1) + ".png").getImage();
        }

        handX = random.nextInt(1100) + 10;

        // Timer ที่ควบคุมทั้งกระโดดและตรวจสอบการชน
        actionTimer = new Timer(20, new ActionListener() {
            boolean jumpingUp = true; // ทิศทางการกระโดด

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isJumping) {
                    if (jumpingUp) {
                        characterY -= 10;
                        if (characterY <= 400) {
                            jumpingUp = false;
                        }
                    } else {
                        characterY += 10;
                        if (characterY >= 540) {
                            characterY = 540;
                            isJumping = false;
                            jumpingUp = true;
                        }
                    }
                }
            
                repaint(); // วาดใหม่ทุกครั้ง
            }    
        });

        actionTimer.start(); // เริ่ม Timer


        try {
            robot = new Robot(); // สร้าง Robot หนึ่งตัวเพื่อควบคุมเมาส์
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bg, 0, 0, this);
        Image currentCharacter = getCurrentCharacter();
        g.drawImage(currentCharacter, movecharacter - getCharacterOffset(), characterY, getCharacterWidth(), characterHeight, this);

        if (ishand) {
            g.drawImage(hand, handX, handY, handWidth, handHeight, this);
        }
    }

    private Image getCurrentCharacter() {
        switch (ingame.textcha) {
            case "c01": return character[0];
            case "c02": return character[1];
            case "c03": return character[2];
            case "c04": return character[3];
            case "c05": return character[4];
            default: return null;
        }
    }

    private int getCharacterWidth() {
        switch (ingame.textcha) {
            case "c01": return c01Width;
            case "c02": return c02Width;
            case "c03": return c03Width;
            case "c04": return c04Width;
            case "c05": return c05Width;
            default: return 0;
        }
    }

    private int getCharacterOffset() {
        switch (ingame.textcha) {
            case "c01": return 100;
            case "c02": return 80;
            case "c03": return 90;
            case "c04": return 95;
            case "c05": return 125;
            default: return 0;
        }
    }

    private boolean checkCollision() {
        // ขอบของมือ
        int handLeft = handX;
        int handRight = handX + handWidth;
        int handTop = handY;
        int handBottom = handY + handHeight; 

       // System.out.println("handLeft " + handLeft + "--------"+"handRight "+handRight);
      //  System.out.println("handTop " + handTop );
        // ขอบของตัวละคร
        int characterLeft = movecharacter ;
        int characterRight = movecharacter + getCharacterWidth();
        int characterBottom = characterY + characterHeight ;
     // System.out.println("characterLeft " + characterLeft + "--------"+"characterRight "+characterRight);
     //  System.out.println("characterBottom " + characterBottom );
      // System.out.println("characterY: " + characterY + ", handTop: " + handTop + ", handBottom: " + handBottom);

        // ตรวจสอบการชนซ้ายขวา
        if (characterRight - 115 > handLeft && characterLeft - 73 < handRight && characterBottom  > handTop) {
            // ชนจากซ้ายไปขวา (ตัวละครเข้าไปด้านซ้ายของมือ)
            if (characterRight - 115 > handLeft && characterLeft < handLeft) {
                movecharacter -= 100; // เลื่อนตัวละครออกทางซ้าย
                moveMouseWithCharacter(-100);
            } 
            // ชนจากขวาไปซ้าย (ตัวละครเข้าไปด้านขวาของมือ)
            else if (characterLeft - 73 < handRight && characterRight > handRight) {
               movecharacter += 100 ; // เลื่อนตัวละครออกทางขวา
              moveMouseWithCharacter(100);
            }
                return true; // มีการชน
            }
            
        return false; // ไม่มีการชน
    }

    // ฟังก์ชันเลื่อนเคอร์เซอร์ตามตัวละคร
    private void moveMouseWithCharacter(int offsetX) {
        try {
            // รับตำแหน่งปัจจุบันของเคอร์เซอร์
            int currentMouseX = java.awt.MouseInfo.getPointerInfo().getLocation().x;
            int currentMouseY = java.awt.MouseInfo.getPointerInfo().getLocation().y;

            // เลื่อนเคอร์เซอร์ไปตามตำแหน่งใหม่ (แกน X เคลื่อนตาม offset)
            robot.mouseMove(currentMouseX + offsetX, currentMouseY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        movecharacter = e.getX();
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
            if (movecharacter - 80 < 0) { // ขอบซ้าย
                movecharacter = 80;
            }
            if (movecharacter + 65 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 65;
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

        // ตรวจสอบการชนเมื่อเลื่อนเมาส์
        if (ishand && checkCollision()) {
            System.out.println("Collision detected!"); // Debugging purpose
        }


        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isJumping) {
            isJumping = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
