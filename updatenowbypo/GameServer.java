import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    tradetime timecount;
    ThreadRain threadRain; // เพิ่มการใช้งาน ThreadRain
    ThreadHand handthread;

    GameServer(){
        timecount = new tradetime(0,40); 
        threadRain = new ThreadRain(timecount); // เรียกใช้งาน ThreadRain
        handthread = new ThreadHand(timecount);
    }

    public static void main(String[] args) {
        System.out.println("Game Server is running...");
        new GameServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            boolean firstClientConnected = false;
             // เริ่มการทำงานของลูกอม
            while (true) {
                new ClientHandler(serverSocket.accept()).start();

                // ตรวจสอบว่าถ้ามี client เข้ามาเชื่อมต่อแล้วค่อยเริ่มจับเวลา
                if (!firstClientConnected) {
                    startBroadcastingTime();
                    startBroadcastingcandy();
                    startBroadcastingHand();
                    firstClientConnected = true; // ตั้งค่าเป็น true หลังจาก client แรกเชื่อมต่อ
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBroadcastingTime() {
        timecount.startdown(); // เริ่มนับถอยหลัง
        new Thread(() -> {
            while (timecount.isend) {
                try {
                    Thread.sleep(1000); // ส่งข้อมูลทุกวินาที
                    broadcastTime(timecount.minutes, timecount.seconds);
               
                    System.out.println("Broadcasting time: " + timecount.minutes + "m " + timecount.seconds + "s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            broadcastTimeEnd(timecount.isend);

        }).start();
    }
    
    private void broadcastTime(int minutes, int seconds) {
        String timeMessage = "time," + minutes + "," + seconds;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(timeMessage);
            }
        }
    }

    private void broadcastTimeEnd(boolean isend) {
        String timeendMessage = "timeend," + isend;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(timeendMessage);
            }
        }
    }


    private void startBroadcastingcandy() {
        threadRain.start();
        new Thread(() -> {
            while (timecount.isend) {
                try {
                   
                   for(int i=0;i<threadRain.Candy;i++){
                    broadcastCandyPosition(); // ส่งตำแหน่งลูกอมทุกวินาทีด้วย
                   }
                   broadcastDonutPosition();
                   broadcastPumpkinPosition();

                    Thread.sleep(20); // ส่งข้อมูลทุกวินาที
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
    private void broadcastCandyPosition() {
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                for ( int i = 0; i < threadRain.Candy; i++) {
                    String candyMessage = "candy," + i + "," + threadRain.Ranx[i] + "," + 
                                           threadRain.Rany[i] + "," + threadRain.ranspeed[i]+ "," + threadRain.iscandy[i];
                    clientOut.println(candyMessage);
                }
            }
        }
    }

     // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
     private void broadcastDonutPosition() {
        synchronized (clientWriters) {  
            for (PrintWriter clientOut : clientWriters) {
              
                // ส่งตำแหน่งและความเร็วของโดนัทและฟักทอง
                String donutMessage = " ," + threadRain.donutX + "," + threadRain.donutY + "," + threadRain.donutSpeed  + "," + threadRain.isdonut;
                clientOut.println(donutMessage);
         
            }
        }
    }

     // ส่งตำแหน่งลูกอมไปยัง Client ทั้งหมด
     private void broadcastPumpkinPosition() {
        synchronized (clientWriters) { 
            for (PrintWriter clientOut : clientWriters) { 
                String pumpkinMessage = "  ," + threadRain.pumpkinX + "," + threadRain.pumpkinY + "," + threadRain.pumpkinSpeed  + "," + threadRain.ispumpkin;
                clientOut.println(pumpkinMessage);
            }
        }
    }


    private void startBroadcastingHand() {
    handthread.start();
        new Thread(() -> {
            while (timecount.isend) {
                if (handthread.ishand) {
                    try {
                        broadcastHand(handthread.handX, handthread.handY); 
                        broadcastIsHand(handthread.ishand);
                        Thread.sleep(4000); // วาดมืออยู่ 2 วินาที
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
               else{
                    try {
                        broadcastHand(handthread.handX, handthread.handY); 
                        broadcastIsHand(handthread.ishand);
                        Thread.sleep(6000); // หายไป 6 วินาที
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
               }
                
            }
        }).start();
    }
    
    private void broadcastHand(int handX, int handY) {
        String handMessage = "hand," + handX + "," + handY;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(handMessage);
            }
        }
    }

    private void broadcastIsHand(boolean ishand) {
        String handMessage = "handend," + ishand;
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(handMessage);
            }
        }
    }

    // กระจายข้อมูลไปยังผู้เล่นคนอื่น ๆ ว่าลูกอมชิ้นนี้ถูกเก็บแล้ว
    public void broadcastCandyCollision(int index) {
        // อัปเดตสถานะ iscandy ของลูกอมชิ้นนี้
        threadRain.iscandy[index] = false;
        
        // สร้างข้อความที่จะแจ้งให้ผู้เล่นทุกคนทราบ
        String candyCOLLISIONMessage = "CANDY_COLLISION,"  + index + "," + " false";
        
        // ใช้ synchronized เพื่อป้องกันปัญหาการเข้าถึงพร้อมกัน
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(candyCOLLISIONMessage); // ส่งข้อความไปยัง client ทุกคน
            }
        }
    }

        // กระจายข้อมูลไปยังผู้เล่นทุกคนว่าโดนัทถูกเก็บแล้ว
    public void broadcastDonutCollision() {

        threadRain.isdonut = false;

        String donutMessage = "DONUT_COLLISION," + "false";
        
        synchronized (clientWriters) {
            for (PrintWriter clientOut : clientWriters) {
                clientOut.println(donutMessage); // ส่งข้อมูลไปยัง client ทุกคน
            }
        }
    }

        // กระจายข้อมูลไปยังผู้เล่นทุกคนว่าโดนัทถูกเก็บแล้ว
        public void broadcastPumpkinCollision() {
            threadRain.ispumpkin = false;
            
            String pumpkinMessage = "PUMPKIN_COLLISION," + "false";
            
            synchronized (clientWriters) {
                for (PrintWriter clientOut : clientWriters) {
                    clientOut.println(pumpkinMessage); // ส่งข้อมูลไปยัง client ทุกคน
                }
            }
        }


    

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String playerName;
        private String characterCode;
        private int x, y;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Receive player name and character code
                playerName = in.readLine();
                characterCode = in.readLine();

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(",");
                    if (parts.length == 4) {
                        playerName = parts[0];
                        x = Integer.parseInt(parts[1]);
                        y = Integer.parseInt(parts[2]);
                        characterCode = parts[3];
                        broadcastPosition();
                    }else if (parts[0].equals("CANDY_COLLISION")) {
                   
                        int  index = Integer.parseInt(parts[1]);
                        broadcastCandyCollision(index);
                    } else if (parts[0].equals("DONUT_COLLISION")) {
                        broadcastDonutCollision();
                    }else if (parts[0].equals("PUMPKIN_COLLISION")) {
                        broadcastPumpkinCollision();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Remove the PrintWriter from the set when the player disconnects
                    synchronized (clientWriters) {
                        if (out != null) {
                            clientWriters.remove(out);
                        }
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastPosition() {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(playerName + "," + x + "," + y + "," + characterCode);
                }
            }
        }
    }
}