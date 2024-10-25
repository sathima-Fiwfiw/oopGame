import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Map; 
import java.awt.Font;
import java.awt.Robot;

public class GameClient extends JFrame {
    private static final int SERVER_PORT = 12345; // พอร์ตที่ใช้ในการเชื่อมต่อกับเซิร์ฟเวอร์
    private CollisionCheckerThread collisionChecker;
    private JPanel panel;
    int movecharacter = 0, characterY = 540;
    private Socket socket; // ใช้เชื่อมต่อกับเซิร์ฟเวอร์
    private PrintWriter out; // ใช้ส่งข้อมูลออกไปยังเซิร์ฟเวอร์
    private Map<String, PlayerPoint> otherPlayers = new HashMap<>(); // แผนที่สำหรับเก็บตำแหน่งของผู้เล่นคนอื่น
    private String playerName; // Name of this player
    private String characterCode; // Character code
    private String serverIP; // Server IP address
    Image bg, timeUP, hand ,Donut, Pumpkin , ready, showwinner;
    Image[] character = new Image[5];
    boolean isJumping = false;
    Timer actionTimer , showTimerUP;
    private int remainingMinutes;
    private int remainingSeconds;
    boolean remainEnd = true;
    boolean remainhand = true;
    int Candys = 5;
    Image[] Candy =new Image[Candys];
    int posicandyX[] =new int[Candys];
    int posicandyY[] =new int[Candys];
    double speedcandy[] =new double[Candys];
    boolean[] iscandy = new boolean[Candys];
    int indexcandy;
    int handgrostX , handgrostY;
    private Robot robot;
    int dountx = 20 , dounty = 20;
    int pumpkinx = 20, pumpkiny = 20;
    int Score;
    boolean isdonut;
    boolean ispumpkin;
    boolean isReady;
    boolean showTimerUPimage = true;
    String winnerName = " ";
    String winnerScore = " ";
    String winnerCharacterCode = " ";

    // Adjusted constructor to accept name, character code, and IP from Ipgame
    public GameClient(String characterCode, String playerName, String serverIP) {
        this.playerName = playerName;
        this.characterCode = characterCode;
        this.serverIP = serverIP;
        
        collisionChecker = new CollisionCheckerThread(this);
        

        loadImages();
        createAndShowGUI(); // สร้าง GUI และแสดง
        connectToServer(); // เชื่อมต่อกับเซิร์ฟเวอร์
        collisionChecker.start();
        actionTimer = new Timer(15, new ActionListener() {
               boolean jumpingUp = true; // ทิศทางการกระโดด
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isJumping) {
                    if (jumpingUp) {
                        characterY -= 10; // Move up
                        if (characterY <= 385) {
                            jumpingUp = false; // Change direction when reaching peak
                        }
                    } else {
                        characterY += 10; // Move down
                         // ตรวจสอบการชนกับมือเมื่อกำลังตกลงมา

                         if (remainhand && checkCollisionFromAbove()) {
                            characterY = handgrostY - 250; // หยุดตัวละครบนมือของผี
                            isJumping = false;
                            jumpingUp = true; // รีเซ็ตทิศทางการกระโดด

                            if (movecharacter < handgrostX) { // If the character is on the left side of the hand
                                movecharacter -= 100; // Bounce left
                                characterY = 540;
                            } else { // If the character is on the right side of the hand
                                movecharacter += 100; // Bounce right
                                characterY = 540;
                            }
                        }

                        if (characterY >= 540) { // Landing on the ground
                            characterY = 540;
                            isJumping = false;
                            jumpingUp = true; // Reset jump direction
                        }
                    }

                    for(int i = 0; i < Candys; i++){
                        checkCollectCandy(i);
                    }
                checkCollectDonut();
                checkCollectPumpkin();

                }

                sendPosition(); 
                panel.repaint(); // Repaint the panel every tick
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
        //collisionChecker.stopChecking();หยุดเช็คลูกอม
    }

    void loadImages() {
            // โหลดภาพพื้นหลังและตัวละคร
            bg = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "ingame" + File.separator + "bgingame.png");

            ready = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
             File.separator + "ingame" + File.separator + "ready.png");

            for (int i = 0; i < character.length; i++) {
                character[i] = new ImageIcon("C:\\oopGame\\imageip/" + (i + 1) + ".png").getImage(); 
            }
            for (int i = 0; i < Candys; i++) {
                Candy[i] = new ImageIcon("C:\\oopGame\\imageRain/" + ((i % 10) + 1)  + ".png").getImage(); 
            }

            timeUP = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "ingame" + File.separator + "time.png");

            hand = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "ingame" + File.separator + "handgrost.png");

            Donut = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "imageRain" + File.separator + "donut.png");
            
            Pumpkin  = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "imageRain" + File.separator + "pumpkin.png");

            showwinner = Toolkit.getDefaultToolkit().createImage(System.getProperty("user.dir") +
            File.separator + "ingame" + File.separator + "ShowScore.png");
    }

    private void createAndShowGUI() {
        setTitle("Game Client");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the current player
                g.drawImage(bg, 0, 0, this);
                if (isReady) {
                    g.drawImage(ready,250,200,this);
                }
                else{
                    actionTimer.start(); // Start the timer
                    g.drawImage(character[getCharacterIndex(characterCode)], movecharacter - getCharacterOffset(characterCode), characterY, getCharacterWidth(characterCode), 250, this); // วาดตัวผู้เล่น
                    g.setColor(Color.BLACK);
                    Font font = new Font("Berlin sans FB Demi", Font.BOLD, 20); 
                    g.setFont(font); 
                    g.drawString(playerName, movecharacter - getCharacterOffset(characterCode) + 50 , characterY - 20); // วาดชื่อผู้เล่นเหนือรูปตัวละคร
                    
                
                
                    // วาดลูกอมที่ตำแหน่งที่ได้รับจากเซิร์ฟเวอร์
                    for (int i = 0; i < Candys; i++) {
                        if (iscandy[i]) {
                            g.drawImage(Candy[i], posicandyX[i], posicandyY[i], 60, 35, this);
                        }
                    }
                    if (isdonut) {
                        g.drawImage(Donut, dountx, dounty, 50, 50, this);
                    }
                    if (ispumpkin) {
                        g.drawImage(Pumpkin,pumpkinx, pumpkiny, 50, 55, this);
                    }
                    
                    if (remainhand) {
                        g.drawImage(hand, handgrostX - 10, handgrostY, 80, 100, this);
                    }

                    // วาดผู้เล่นคนอื่น
                    for (Map.Entry<String, PlayerPoint> entry : otherPlayers.entrySet()) {
                        PlayerPoint p = entry.getValue();
                        String otherName = entry.getKey();

                        Image otherCharacterImage = getCharacterImageBasedOnCode(p.characterCode); // ใช้ characterCode ของผู้เล่นอื่น
                        g.drawImage(otherCharacterImage, p.x - getCharacterOffset(p.characterCode), p.y, getCharacterWidth(p.characterCode), 250, this); // วาดตัวผู้เล่น
                        g.setFont(font); 
                        g.drawString(otherName, p.x - getCharacterOffset(p.characterCode) + 50, p.y - 20); // วาดชื่อผู้เล่นเหนือรูปตัวละคร
                    
                    }

                    // Draw the current time
                    Font fonttime = new Font("Berlin sans FB Demi", Font.BOLD, 40); 
                    g.setFont(fonttime);
                    g.drawString(String.format("%02d:%02d", remainingMinutes, remainingSeconds), 300, 55);

                    String ScoreStr = Integer.toString(Score);
                    g.drawString("  "+ScoreStr, 1110, 55);
                }

                if (!remainEnd) {
                    if (showTimerUPimage) {
                        g.drawImage(timeUP, 320, 100, 800, 500, this);
                        showTimerUP.start();
                        sendScoreResult();
                    }else{
                       
                         System.out.println("WINNER");
                        g.drawImage(showwinner, 0, 50,1440,750, this);
                        Font  fontWinner = new Font("Berlin sans FB Demi", Font.BOLD, 40); 
                        g.setFont(fontWinner);
                        g.drawString(winnerName, 720, 345);
                        g.drawString(winnerScore, 790, 470);

                        if ("c01".equals(winnerCharacterCode)) {
                            g.drawImage(character[getCharacterIndex(winnerCharacterCode)], 500, 250, getCharacterWidth(winnerCharacterCode)-70, 250 - 70, this);
                        }
                        else if ("c05".equals(winnerCharacterCode)) {
                            g.drawImage(character[getCharacterIndex(winnerCharacterCode)], 500, 250, getCharacterWidth(winnerCharacterCode)-40, 250 - 70, this);
                        }
                        else{
                            g.drawImage(character[getCharacterIndex(winnerCharacterCode)], 520, 250, getCharacterWidth(winnerCharacterCode)-40, 250 - 70, this);
                        }
                        
        
                    }
                    
                }

                repaint();
            }
        };

        panel.setPreferredSize(new Dimension(1440, 810));
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mouse motion listener to track mouse movement
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (remainEnd) {
                     movecharacter = e.getX(); // Center the character on the mouse, using offset
                }

            // ตัวละคร c01 - เช็คขอบ
            if ("c01".equals(characterCode)) {
                if (movecharacter - 100 < 0) { // ขอบซ้าย
                    movecharacter = 100;
                }
                if (movecharacter + 90 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 90;
                }
            }
            // ตัวละคร c02 - เช็คขอบ
            else if ("c02".equals(characterCode)) {
                if (movecharacter - 80 < 0) { // ขอบซ้าย
                    movecharacter = 80;
                }
                if (movecharacter + 70 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 70;
                }
            }
            // ตัวละคร c03 - เช็คขอบ
            else if ("c03".equals(characterCode)) {
                if (movecharacter - 81 < 0) { // ขอบซ้าย
                    movecharacter = 81;
                }
                if (movecharacter + 67 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 67;
                }
            }
            // ตัวละคร c04 - เช็คขอบ
            else if ("c04".equals(characterCode)) {
                if (movecharacter - 83 < 0) { // ขอบซ้าย
                    movecharacter = 83;
                }
                if (movecharacter + 45 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 45;
                }
            }
            // ตัวละคร c05 - เช็คขอบ
            else if ("c05".equals(characterCode)) {
                if (movecharacter - 80 < 0) { // ขอบซ้าย
                    movecharacter = 80;
                }
                if (movecharacter + 70 > panel.getWidth()) { // ขอบขวา
                    movecharacter = panel.getWidth() - 70;
                }
            }

            if (remainhand && checkCollision() ) {
                System.out.println("Collision detected!"); // Debugging purpose
            }
            //เช็คตัวละครโดนลูกอม
            for(int i = 0; i < Candys; i++)
            {
                checkCollectCandy(i);
            }

            checkCollectDonut();
            checkCollectPumpkin();
                
                sendPosition(); // ส่งตำแหน่งผู้เล่นไปยังเซิร์ฟเวอร์
                panel.repaint();
            }
        });

                // เพิ่ม mouseClicked() สำหรับการตรวจจับคลิกเมาส์
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (remainEnd) {
                    if (!isJumping) {
                        isJumping = true; // กระโดดเมื่อไม่ได้อยู่ในสถานะการกระโดด
                    }
                }
                    
            }
        });
    }

        // เพิ่มเมธอด getCharacterWidth() และ getCharacterOffset()
        private int getCharacterWidth(String characterCodeID) {
            switch (characterCodeID) { 
                case "c01": return 200;
                case "c02": return 150;
                case "c03": return 150;
                case "c04": return 120;
                case "c05": return 160;
                default: return 0;
            }
        }

        private int getCharacterOffset(String characterCodeID) {
            switch (characterCodeID) {
                case "c01": return 100;
                case "c02": return 80;
                case "c03": return 80;
                case "c04": return 80;
                case "c05": return 80;
                default: return 0;
            }
        }


    private void connectToServer() {
        try {
            socket = new Socket(serverIP, SERVER_PORT); // เชื่อมต่อไปยังเซิร์ฟเวอร์ที่กำหนด
            out = new PrintWriter(socket.getOutputStream(), true); // สร้าง PrintWriter เพื่อส่งข้อมูลออกไปยังเซิร์ฟเวอร์
            out.println(playerName); // ส่งชื่อผู้เล่นไปยังเซิร์ฟเวอร์
            out.println(characterCode); // ส่งรหัสตัวละครไปยังเซิร์ฟเวอร์
            new Thread(this::receivePositionUpdates).start(); // สร้าง thread ใหม่เพื่อรับข้อมูลจากเซิร์ฟเวอร์
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPosition() {
        out.println(playerName + "," + movecharacter + "," + characterY + "," + characterCode); // ส่งชื่อผู้เล่น ตำแหน่ง x, y และโค้ดตัวละคร
    }

    public void sendCandyCollisionToServer(int index) {
        // ส่งข้อความไปยังเซิร์ฟเวอร์
        String message = "CANDY_COLLISION," + index;
        out.println(message); 
    }

    void sendScoreResult(){
        // สร้างข้อความที่ประกอบด้วย score, name, และ character
        String SCOREmessage = "SCORE," + playerName + "," + characterCode + "," + Score;
        out.println(SCOREmessage);
    }

    private void updateTimerDisplay(int minutes, int seconds) {
        this.remainingMinutes = minutes;
        this.remainingSeconds = seconds;
    }

    private void updateCandyPosition(int x, int y,int index,double speed,boolean iscandy) {
        this.posicandyX[index] = x;
        this.speedcandy[index] = speed;
        this.posicandyY[index] = (int) (y + speedcandy[index]); 
        this.iscandy[index] = iscandy;
        panel.repaint(); // Repaint the panel to update candy position
    }

    private void updatetimeend(boolean isend){
        this.remainEnd = isend;
    }

    private void updatehand(int handx , int handy){
       this.handgrostX = handx;
       this.handgrostY = handy;
    }

    private void updateIshand(boolean ishand){
        this.remainhand = ishand;
     }

    private void updateDonutPosition(int donutX, int donutY, double donutSpeed, boolean isdonut) {
        this.dountx = donutX;
        this.dounty = (int) (donutY + donutSpeed); // อัปเดตตำแหน่ง Y ของโดนัท
        this.isdonut = isdonut;
        panel.repaint();
    }
    
    private void updatePumpkinPosition(int pumpkinX, int pumpkinY, double pumpkinSpeed,  boolean ispumpkin) {
        this.pumpkinx = pumpkinX;
        this.pumpkiny = (int) (pumpkinY + pumpkinSpeed); // อัปเดตตำแหน่ง Y ของฟักทอง
        this.ispumpkin = ispumpkin;
        panel.repaint();
    }
    private void receivePositionUpdates() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                String[] parts = message.split(","); // แยกข้อมูลที่ได้รับเป็นส่วนๆ
                if (parts.length == 4) {
                    String otherPlayerName = parts[0];
                    int otherX = Integer.parseInt(parts[1]);
                    int otherY = Integer.parseInt(parts[2]);
                    String otherCharacterCode = parts[3];

                    // อัปเดตตำแหน่งของผู้เล่นคนอื่น
                    PlayerPoint playerPoint = new PlayerPoint(otherX, otherY, otherCharacterCode);
                    otherPlayers.put(otherPlayerName, playerPoint); // เก็บข้อมูลผู้เล่นอื่นลงในแผนที่
                    panel.repaint(); // วาดใหม่เพื่ออัปเดตการแสดงผล
                }else if (parts[0].equals("Ready")) {
                    // Update 
                    boolean isshowReadyImage = Boolean.parseBoolean(parts[1]);

                    isReady = isshowReadyImage; // อัปเดตค่า isReady
                }
                else if (parts[0].equals("time")) {
                    // Update time
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);
                    updateTimerDisplay(minutes, seconds); // Update the timer display
                } else if (parts[0].equals("timeend")) {
                  
                    boolean isend = Boolean.parseBoolean(parts[1]); // รับตำแหน่ง index ของลูกอม
                    updatetimeend(isend);
                    panel.repaint();
                }
                else if (parts[0].equals("hand")) {
                    
                    int handx = Integer.parseInt(parts[1]);
                    int handy = Integer.parseInt(parts[2]);
                    updatehand(handx , handy);
                    panel.repaint();
                }else if (parts[0].equals("handend")) {
                   
                    boolean ishand = Boolean.parseBoolean(parts[1]);
                    updateIshand(ishand);
                    panel.repaint();
                }
                if (parts[0].equals("candy")) {
                    // อัปเดตตำแหน่งและความเร็วลูกอมที่ได้รับจากเซิร์ฟเวอร์
                    int candyIndex = Integer.parseInt(parts[1]); // รับตำแหน่ง index ของลูกอม
                    int candyX = Integer.parseInt(parts[2]); // รับตำแหน่ง x
                    int candyY = Integer.parseInt(parts[3]); // รับตำแหน่ง y
                    double candySpeed = Double.parseDouble(parts[4]); // รับความเร็วของลูกอม
                    boolean iscandy = Boolean.parseBoolean(parts[5]);
                
                    // อัปเดตตำแหน่งลูกอมในแผงหรือพื้นที่แสดงผล
                    updateCandyPosition(candyX, candyY, candyIndex,candySpeed,iscandy);
                    panel.repaint();
                } else if (parts[0].equals(" ")) {
                    // อัปเดตตำแหน่งและความเร็วลูกอมที่ได้รับจากเซิร์ฟเวอร์
                    int donutX = Integer.parseInt(parts[1]); // รับตำแหน่ง x
                    int donutY = Integer.parseInt(parts[2]); // รับตำแหน่ง y
                    double donutSpeed = Double.parseDouble(parts[3]); // รับความเร็วของลูกอม
                    boolean isdonut = Boolean.parseBoolean(parts[4]);

                    updateDonutPosition(donutX, donutY, donutSpeed ,isdonut);
                    panel.repaint();
                }else if (parts[0].equals("  ")) {
                    // อัปเดตตำแหน่งและความเร็วลูกอมที่ได้รับจากเซิร์ฟเวอร์
                    int pumpkinX = Integer.parseInt(parts[1]); // รับตำแหน่ง x
                    int pumpkinY = Integer.parseInt(parts[2]); // รับตำแหน่ง y
                    double pumpkinSpeed = Double.parseDouble(parts[3]); // รับความเร็วของลูกอม
                    boolean ispumpkin = Boolean.parseBoolean(parts[4]);

                    updatePumpkinPosition(pumpkinX, pumpkinY, pumpkinSpeed, ispumpkin);
                    panel.repaint();
                }else if (parts[0].equals("CANDY_COLLISION")) {
                    int index = Integer.parseInt(parts[1]); // ดัชนีของลูกอม
                    boolean isCandyCollected = Boolean.parseBoolean(parts[2]); // สถานะลูกอม
            
                    // อัปเดตสถานะของลูกอม
                    if (!isCandyCollected) {
                        iscandy[index] = false; // ลูกอมถูกเก็บแล้ว ไม่ให้วาดอีก
                    }
                }else if (parts[0].equals("DONUT_COLLISION")) {
                    boolean isDonutCollected = Boolean.parseBoolean(parts[1]); // สถานะลูกอม
            
                    if (!isDonutCollected) {
                        isdonut = false;  // อัปเดตสถานะว่าโดนัทถูกเก็บแล้ว
                    }
                }else if (parts[0].equals("PUMPKIN_COLLISION")) {
                    boolean isPumpkinCollected = Boolean.parseBoolean(parts[1]); // สถานะลูกอม
            
                    if (!isPumpkinCollected) {
                        ispumpkin = false;  // อัปเดตสถานะว่าโดนัทถูกเก็บแล้ว
                    }
                }else if (parts[0].equals("WINNER")) {
                    String winnerName = parts[1];
                    String winnerScore = parts[2];
                    String winnerCharacterCode = parts[3];
                
                    // อัปเดตค่าผู้ชนะ
                    this.winnerName = winnerName; 
                    this.winnerScore = winnerScore;
                    this.winnerCharacterCode = winnerCharacterCode;
                }
                panel.repaint(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // เมธอดช่วยในการเลือกรูปตัวละครตามรหัส
     private Image getCharacterImageBasedOnCode(String code) {
        int index = getCharacterIndex(code);
        return character[index];
    }

    private int getCharacterIndex(String code) {
        switch (code) {
            case "c02":
                return 1;
            case "c03":
                return 2;
            case "c04":
                return 3;
            case "c05":
                return 4;
            default:
                return 0;
        }
    }

     //เช็คโดนมือผี
     private boolean checkCollision() {
        // ขอบของมือ
        int handLeft = handgrostX;
        int handRight = handgrostX + 80;
        int handTop = handgrostY;
        // ขอบของตัวละคร
        int characterLeft = movecharacter ;
        int characterRight = movecharacter + getCharacterWidth(characterCode);
        int characterBottom = characterY + 250 ;
        
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
                    characterY = handgrostY - 250;
                    movecharacter += 100; // Bounce right
                    characterY = 540;
                    Score -= 70;
                    return true;
            }
        
        return false; // ไม่มีการชน
    }

     boolean checkCollisionFromAbove() {
        int handTop = handgrostY; // ขอบบนของมือ
        int handLeft = handgrostX;
        int handRight = handgrostX + 80;
    
        int characterBottom = characterY + 250; // ขอบล่างของตัวละคร
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth(characterCode);
    
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
        int candyBottom = posicandyY[index] + 35;
        int candyLeft = posicandyX[index];
        int candyRight =  posicandyX[index] + 60;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth(characterCode);
        int characterTop = characterY;
    
        // ตรวจสอบว่าลูกอมชนกับตัวละครและยังอยู่ในสถานะที่สามารถเก็บได้อยู่
        if ( iscandy[index] && candyBottom >= characterTop && candyRight >= characterLeft - 75 && candyLeft <= characterRight - 125) {
            // ลูกอมชนกับตัวละคร
            iscandy[index] = false; 
            Score += 50; 
            // ส่งสถานะไปยังเซิร์ฟเวอร์
            sendCandyCollisionToServer(index);
        }
    }
    //เช็คโดนัทโดนตัวละคร
    void checkCollectDonut(){
        int DonutBottom =  dounty + 50;
        int DonutLeft = dountx;
        int DonutRight = dountx + 50;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth(characterCode);
        int characterTop = characterY;
        if (isdonut && DonutBottom >= characterTop && DonutRight >= characterLeft - 75 && DonutLeft <= characterRight - 125) {
           
            isdonut = false; 
            Score += 200; 

            out.println("DONUT_COLLISION"); //ส่งไปบอกฝั่ง sever
        }

    }
    //เช็คฝักทองโดนตัวละคร
    void checkCollectPumpkin(){
        int pumpkinBottom =  pumpkiny + 55;
        int pumpkintLeft = pumpkinx;
        int pumpkinRight = pumpkinx + 50;
        
        int characterLeft = movecharacter;
        int characterRight = movecharacter + getCharacterWidth(characterCode);
        int characterTop = characterY;

        if (ispumpkin && pumpkinBottom  >= characterTop && pumpkinRight >= characterLeft - 75 && pumpkintLeft<= characterRight - 125) {
          
           ispumpkin = false; 
           Score -= 150; 

           out.println("PUMPKIN_COLLISION"); //ส่งไปบอกฝั่ง sever
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

     // คลาสย่อยสำหรับเก็บตำแหน่งและโค้ดตัวละครของผู้เล่น
     private static class PlayerPoint {
        int x, y;
        String characterCode;
        PlayerPoint(int movecharacter, int characterY, String characterCode) {
            this.x = movecharacter;
            this.y = characterY;
            this.characterCode = characterCode;
        }
    }
}

    class CollisionCheckerThread extends Thread {
    private boolean isRunning = true;
    private GameClient gameClient;  // อ้างอิงไปยัง client ที่ต้องการเช็คการชน

    public CollisionCheckerThread(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void run() {
        while (isRunning) {
            // ตรวจสอบการชนกันทุกครั้ง
            if (gameClient.remainEnd) {
                for(int i = 0; i < gameClient.Candys; i++){
                    gameClient.checkCollectCandy(i);
                }
                    gameClient.checkCollectDonut();
                    gameClient.checkCollectPumpkin();

                try {
                    // หยุดชั่วคราวเล็กน้อยเพื่อไม่ให้เช็คถี่เกินไป
                    Thread.sleep(20); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        }
    }

    public void stopChecking() {
        isRunning = false;
    }
}