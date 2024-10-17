import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;

public class itselfgame extends JFrame {
    String characterID;
    String nameUser;
    String IP;
    PanelGame pInGame;
    tradetime timecount;
    ThreadRain candyFallThread ;
    ThreadHand handgrost;

    itselfgame(String characterID,String nameUser,String IP) {
        this.characterID = characterID;
        this.nameUser = nameUser;
        this.IP = IP;

        setSize(1450, 840);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        timecount = new tradetime(0,50); 
        candyFallThread = new ThreadRain(timecount);
        handgrost = new ThreadHand(timecount);
        pInGame = new PanelGame(this,timecount,candyFallThread,handgrost);
        pInGame.addMouseMotionListener(pInGame);
        pInGame.addMouseListener(pInGame);
        add(pInGame);
    }

    public static void main(String[] args) {
        new itselfgame("c02","PODER_555","192.0011255").setVisible(true);
    }
}

class PanelGame extends JPanel implements MouseMotionListener, MouseListener {
    itselfgame ingame;
    tradetime timecount;
    ThreadRain candyFallThread ;
    ThreadHand handgrost;
    Image[] character = new Image[5];
    Image bg, hand, timeUP , donut, pumpkin , ready,showwinner;
    int movecharacter = 0, characterY = 540;
    int characterHeight = 250;
    int c01Width = 200, c02Width = 150, c03Width = 150, c04Width = 120, c05Width = 160;
    int candyWidth = 60, candyHeight = 35;
    boolean isJumping = false;
    Timer actionTimer;
    Timer showReadyTimer;
    Timer showTimerUP;
    int handWidth = 80, handHeight = 100;
    Random random = new Random();
    private Robot robot;
    int Candy=2;   
    Image[] CandyRain =new Image[Candy];
    int Score = 0;
    int donutWidth = 50, donutHeight = 50;
    int pumpkinWidth = 50, pumpkinHeight = 55;
    boolean showReadyImage = true;
    boolean showTimerUPimage = true;
    Image currentCharacter;

    PanelGame(itselfgame ingame,tradetime timecount , ThreadRain candyFallThread,  ThreadHand handgrost) {
        this.ingame = ingame;
        this.timecount = timecount;
        this.candyFallThread = candyFallThread;
        this.handgrost = handgrost;
        bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "bgingame.png");

        hand = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "handgrost.png");

        timeUP = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "time.png");

        ready = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "ready.png");

        showwinner = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "ingame" + File.separator + "ShowScore.png");
        
        donut = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imageRain" + File.separator + "donut.png");
        
        pumpkin  = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
        File.separator + "imageRain" + File.separator + "pumpkin.png");

       

        for (int i = 0; i < character.length; i++) {
            character[i] = new ImageIcon("C:/oopGame/imageip/" + (i + 1) + ".png").getImage();
        }
        
       for (int i = 0; i < CandyRain.length; i++) {
        CandyRain[i] = new ImageIcon("C:/oopGame/imageRain/" + ((i % 10) + 1) + ".png").getImage();
        candyFallThread.iscandy[i] = true;
       }


           // เริ่ม thread สำหรับการตกของลูกอม
       // ThreadRain candyFallThread = new ThreadRain(this,timecount);
        
        currentCharacter = getCurrentCharacter();

               // Timer เพื่อแสดงภาพ "ready" เป็นเวลา 3 วินาที
        showReadyTimer = new Timer(3000, new ActionListener() {
                @Override
            public void actionPerformed(ActionEvent e) {
               showReadyImage = false; // ซ่อนภาพ "ready" หลังจาก 3 วินาที
               candyFallThread.start();
               timecount.startdown();
               actionTimer.start(); // เริ่ม Timer สำหรับเกม
               handgrost.start(); // เริ่ม Timer ของมือ
              
               repaint(); // วาดใหม่ทุกครั้ง
           }
        });
        showReadyTimer.setRepeats(false); // ทำให้ Timer นี้ทำงานเพียงครั้งเดียว
        showReadyTimer.start(); // เริ่มการทำงานของ Timer
       
        // Timer ที่ควบคุมทั้งกระโดดและตรวจสอบการชน
        actionTimer = new Timer(20, new ActionListener() {
            boolean jumpingUp = true; // ทิศทางการกระโดด

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isJumping) {
                    if (jumpingUp) {
                        characterY -= 10;
                        if (characterY <= 385) {
                            jumpingUp = false; // เปลี่ยนทิศทางเมื่อตัวละครกระโดดถึงจุดสูงสุด
                        }
                    } else {
                        // ตัวละครกำลังตกลงมา
                        characterY += 10;
        
                        // ตรวจสอบการชนกับมือเมื่อกำลังตกลงมา
                        if (handgrost.ishand && checkCollisionFromAbove()) {
                            characterY = handgrost.handY - characterHeight; // หยุดตัวละครบนมือของผี
                            isJumping = false;
                            jumpingUp = true; // รีเซ็ตทิศทางการกระโดด

                            if (movecharacter < handgrost.handX) { // If the character is on the left side of the hand
                                movecharacter -= 100; // Bounce left
                                characterY = 540;
                            } else { // If the character is on the right side of the hand
                                movecharacter += 100; // Bounce right
                                characterY = 540;
                            }
                        }
        
                        if (characterY >= 540) { // ตกถึงพื้น
                            characterY = 540;
                            isJumping = false;
                            jumpingUp = true;
                        }
                    }

                    for(int i = 0; i < Candy; i++){
                            checkCollectCandy(i);
                    }
                    checkCollectDonut();
                    checkCollectPumpkin();
                }
            
                repaint(); // วาดใหม่ทุกครั้ง
            }    
        });

        
    showTimerUP = new Timer(3000, new ActionListener() {
            @Override
        public void actionPerformed(ActionEvent e) {
            showTimerUPimage = false;
           repaint(); // วาดใหม่ทุกครั้ง
       }
    });
    showTimerUP.setRepeats(false); // ทำให้ Timer นี้ทำงานเพียงครั้งเดียว
   

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

        if (showReadyImage) {
             g.drawImage(ready,250,200,this);
        }
       else{
            for(int i=0;i<Candy;i++)
            {
                if (candyFallThread.iscandy[i]) {
                    g.drawImage(CandyRain[i],candyFallThread.Ranx[i],candyFallThread.Rany[i],candyWidth,candyHeight,this);
                }
            }
            if (candyFallThread.isdonut) {
                g.drawImage(donut, candyFallThread.donutX, candyFallThread.donutY, donutWidth, donutHeight, this);
            }
            if (candyFallThread.ispumpkin) {
                g.drawImage(pumpkin,candyFallThread.pumpkinX, candyFallThread.pumpkinY, pumpkinWidth, pumpkinHeight, this);
            }
            g.drawImage(currentCharacter, movecharacter - getCharacterOffset(), characterY, getCharacterWidth(), characterHeight, this);
            Font font = new Font("Berlin sans FB Demi", Font.BOLD, 20); 
            
            if ("c01".equals(ingame.characterID)) {
                g.setFont(font); // ตั้งค่าฟ้อนให้กับ Graphics
                g.drawString(ingame.nameUser, movecharacter - getCharacterOffset() + 50, characterY - 20);
            }
            else{
                g.setFont(font); // ตั้งค่าฟ้อนให้กับ Graphics
                g.drawString(ingame.nameUser, movecharacter - getCharacterOffset() + 20, characterY - 20);
            }
            Font fonttime = new Font("Berlin sans FB Demi", Font.BOLD, 40); 
            g.setFont(fonttime);
            g.drawString(String.format("%02d:%02d", timecount.minutes, timecount.seconds), 300, 55);

            String ScoreStr = Integer.toString(Score);
            g.drawString("  "+ScoreStr, 1110, 55);
        
            if (handgrost.ishand) {
                g.drawImage(hand, handgrost.handX-10, handgrost.handY, handWidth, handHeight, this);
            }
        }
        
        if (!timecount.isend) {
            if (showTimerUPimage) {
                g.drawImage(timeUP, 320, 100, 800, 500, this);
                showTimerUP.start();
            }
            else{
                g.drawImage(showwinner, 0, 50,1440,750, this);
                Font  fontWinner = new Font("Berlin sans FB Demi", Font.BOLD, 40); 
                g.setFont(fontWinner);
                g.drawString(ingame.nameUser, 720, 345);
                String ScoreStr = Integer.toString(Score);
                g.drawString(ScoreStr, 790, 470);
                if ("c01".equals(ingame.characterID)) {
                    g.drawImage(currentCharacter, 500, 250, getCharacterWidth()-70, characterHeight-70, this);
                }
                else if ("c05".equals(ingame.characterID)) {
                    g.drawImage(currentCharacter, 500, 250, getCharacterWidth()-40, characterHeight-70, this);
                }
                else{
                    g.drawImage(currentCharacter, 520, 250, getCharacterWidth()-40, characterHeight-70, this);
                }
                

            }
        }

        
        repaint();
      
    }


    private Image getCurrentCharacter() {
        switch (ingame.characterID) {
            case "c01": return character[0];
            case "c02": return character[1];
            case "c03": return character[2];
            case "c04": return character[3];
            case "c05": return character[4];
            default: return null;
        }
    }

    private int getCharacterWidth() {
        switch (ingame.characterID) {
            case "c01": return c01Width;
            case "c02": return c02Width;
            case "c03": return c03Width;
            case "c04": return c04Width;
            case "c05": return c05Width;
            default: return 0;
        }
    }

    private int getCharacterOffset() {
        switch (ingame.characterID) {
            case "c01": return 100;
            case "c02": return 80;
            case "c03": return 80;
            case "c04": return 80;
            case "c05": return 80;
            default: return 0;
        }
    }
    //เช็คโดนมือผี
    private boolean checkCollision() {
        // ขอบของมือ
        int handLeft = handgrost.handX;
        int handRight = handgrost.handX + handWidth;
        int handTop = handgrost.handY;
        // ขอบของตัวละคร
        int characterLeft = movecharacter ;
        int characterRight = movecharacter + getCharacterWidth();
        int characterBottom = characterY + characterHeight ;
        
                // ตรวจสอบการชนซ้ายขวา
            if (characterRight - 115 > handLeft && characterLeft - 65 < handRight && characterBottom  > handTop) {
                // ชนจากซ้ายไปขวา (ตัวละครเข้าไปด้านซ้ายของมือ)
                if (characterRight - 115 > handLeft && characterLeft < handLeft) {
                    movecharacter -= 100; // เลื่อนตัวละครออกทางซ้าย
                    moveMouseWithCharacter(-100);
                    Score -= 70;
                } 
                // ชนจากขวาไปซ้าย (ตัวละครเข้าไปด้านขวาของมือ)
                else if (characterLeft - 65 < handRight && characterRight > handRight) {
                    movecharacter += 100 ; // เลื่อนตัวละครออกทางขวา
                    moveMouseWithCharacter(100);
                    Score -= 70;
                }
               
                return true; // มีการชน
            }
            if (characterBottom >= handTop && characterBottom <= handTop + 10 && // ตัวละครอยู่ในระยะใกล้มือ
                characterRight  - 125 > handLeft && characterLeft - 75 < handRight) { // ตัวละครอยู่ภายในขอบซ้ายขวาของมือ
                    characterY = handgrost.handY - characterHeight;
                    movecharacter += 100; // Bounce right
                    characterY = 540;
                    Score -= 70;
                    return true;
            }
        
        return false; // ไม่มีการชน
    }

    private boolean checkCollisionFromAbove() {
        int handTop = handgrost.handY; // ขอบบนของมือ
        int handLeft = handgrost.handX;
        int handRight = handgrost.handX + handWidth;
    
        int characterBottom = characterY + characterHeight; // ขอบล่างของตัวละคร
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth();
    
        // ตรวจสอบว่าตัวละครอยู่เหนือมือ และตกลงไปสัมผัสมือ
        if (characterBottom >= handTop && characterBottom <= handTop + 10 && // ตัวละครอยู่ในระยะใกล้มือ
            characterRight  - 125 > handLeft && characterLeft - 75 < handRight) { // ตัวละครอยู่ภายในขอบซ้ายขวาของมือ
            Score -= 70;
            return true;
        }
    
        return false;
    }
    //เช็คลูกอมโดนตัวละคร
    void checkCollectCandy(int index){
        int candyBottom = candyFallThread.Rany[index] + candyHeight;
        int candyLeft = candyFallThread.Ranx[index];
        int candyRight = candyFallThread.Ranx[index] + candyWidth;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth();
        int characterTop = characterY;
    
        // ตรวจสอบว่าลูกอมชนกับตัวละครและยังอยู่ในสถานะที่สามารถเก็บได้อยู่
        if (candyFallThread.iscandy[index] && candyBottom >= characterTop && candyRight >= characterLeft - 75 && candyLeft <= characterRight - 125) {
            // ลูกอมชนกับตัวละคร
            candyFallThread.iscandy[index] = false; // เปลี่ยนสถานะลูกอมเพื่อไม่ให้ถูกเก็บซ้ำ
            Score += 50; 
            //System.out.println("Collected candy at index: " + index);
            // เพิ่มคะแนนหรือจัดการการเก็บลูกอมที่นี่
        }
    }
    //เช็คโดนัทโดนตัวละคร
    void checkCollectDonut(){
        int DonutBottom =  candyFallThread.donutY + donutHeight;
        int DonutLeft = candyFallThread.donutX;
        int DonutRight = candyFallThread.donutX + donutWidth;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth();
        int characterTop = characterY;
        if (candyFallThread.isdonut && DonutBottom >= characterTop && DonutRight >= characterLeft - 75 && DonutLeft <= characterRight - 125) {
           
            candyFallThread.isdonut = false; 
            Score += 200; 
           
        }

    }
    //เช็คฝักทองโดนตัวละคร
    void checkCollectPumpkin(){
        int pumpkinBottom =  candyFallThread.pumpkinY + pumpkinHeight;
        int pumpkintLeft = candyFallThread.pumpkinX;
        int pumpkinRight = candyFallThread.pumpkinX + pumpkinWidth;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth();
        int characterTop = characterY;

        if (candyFallThread.ispumpkin && pumpkinBottom  >= characterTop && pumpkinRight >= characterLeft - 75 && pumpkintLeft<= characterRight - 125) {
          
            candyFallThread.ispumpkin = false; 
            Score -= 150; 
        }
        
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
        if (timecount.isend) {
            movecharacter = e.getX();
        }
        
         // ตัวละคร c01 - เช็คขอบ
         if ("c01".equals(ingame.characterID)) {
            if (movecharacter - 100 < 0) { // ขอบซ้าย
                movecharacter = 100;
            }
            if (movecharacter + 90 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 90;
            }
        }
        // ตัวละคร c02 - เช็คขอบ
        else if ("c02".equals(ingame.characterID)) {
            if (movecharacter - 80 < 0) { // ขอบซ้าย
                movecharacter = 80;
            }
            if (movecharacter + 65 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 65;
            }
        }
        // ตัวละคร c03 - เช็คขอบ
        else if ("c03".equals(ingame.characterID)) {
            if (movecharacter - 90 < 0) { // ขอบซ้าย
                movecharacter = 90;
            }
            if (movecharacter + 65 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 65;
            }
        }
        // ตัวละคร c04 - เช็คขอบ
        else if ("c04".equals(ingame.characterID)) {
            if (movecharacter - 95 < 0) { // ขอบซ้าย
                movecharacter = 95;
            }
            if (movecharacter + 30 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 30;
            }
        }
        // ตัวละคร c05 - เช็คขอบ
        else if ("c05".equals(ingame.characterID)) {
            if (movecharacter - 125 < 0) { // ขอบซ้าย
                movecharacter = 125;
            }
            if (movecharacter + 40 > this.getWidth()) { // ขอบขวา
                movecharacter = this.getWidth() - 40;
            }
        }

        // ตรวจสอบการชนเมื่อเลื่อนเมาส์
        if(!showReadyImage){
            if (handgrost.ishand && checkCollision() ) {
            System.out.println("Collision detected!"); // Debugging purpose
            }
        }
        
        //เช็คตัวละครโดนลูกอม
        for(int i = 0; i < Candy; i++)
        {
            checkCollectCandy(i);
        }

        checkCollectDonut();
        checkCollectPumpkin();

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (timecount.isend) {
            if (!isJumping) {
                isJumping = true;
            }
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
