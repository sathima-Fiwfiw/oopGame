import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private static final int SERVER_PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    tradetime timecount;
    ThreadRain threadRain; // เพิ่มการใช้งาน ThreadRain

    GameServer(){
        timecount = new tradetime(0,50); 
        threadRain = new ThreadRain(timecount); // เรียกใช้งาน ThreadRain
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
                    timecount.startdown(); // เริ่มนับถอยหลัง
                    startBroadcastingTime();
                    startBroadcastingcandy();
                    threadRain.start();
                    firstClientConnected = true; // ตั้งค่าเป็น true หลังจาก client แรกเชื่อมต่อ
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBroadcastingTime() {
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
      


    private void startBroadcastingcandy() {
        new Thread(() -> {
            while (timecount.isend) {
                try {
                    Thread.sleep(20); // ส่งข้อมูลทุกวินาที
                   for(int i=0;i<threadRain.Candy;i++){
                    broadcastCandyPosition(); // ส่งตำแหน่งลูกอมทุกวินาทีด้วย
                   
                   }
                   broadcastDonutPosition();
                   broadcastPumpkinPosition();
                  
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
                    String candyMessage = "candy," + i + "," + threadRain.Ranx[i] + "," + threadRain.Rany[i] + "," + threadRain.ranspeed[i];
                    clientOut.println(candyMessage);
                }
              
            }
        }
    }
                // ส่งตำแหน่งและความเร็วของโดนัทให้กับ Client ทั้งหมด
        private void broadcastDonutPosition() {
            synchronized (clientWriters) {
                for (PrintWriter clientOut : clientWriters) {
                    // สร้างข้อความสำหรับโดนัท
                    String donutMessage = " ," + threadRain.donutX + "," + threadRain.donutY + "," + threadRain.donutSpeed;

                    // ส่งข้อความไปยัง Client
                    clientOut.println(donutMessage);
                }
            }
        }

        // ส่งตำแหน่งและความเร็วของฟักทองให้กับ Client ทั้งหมด
        private void broadcastPumpkinPosition() {
            synchronized (clientWriters) {
                for (PrintWriter clientOut : clientWriters) {
                    // สร้างข้อความสำหรับฟักทอง
                    String pumpkinMessage = "  ," + threadRain.pumpkinX + "," + threadRain.pumpkinY + "," + threadRain.pumpkinSpeed;

                    // ส่งข้อความไปยัง Client
                    clientOut.println(pumpkinMessage);
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
