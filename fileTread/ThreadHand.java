import javax.swing.Timer;
import java.util.Random;
class ThreadHand extends Thread{
    tradetime timecount;
    Timer handTimer;
    int handX , handY = 710;
    boolean ishand = false; 
    Random random = new Random();

    ThreadHand(tradetime timecount){
        this.timecount = timecount;
    }

    @Override
    public void run() {
        while (timecount.isend) {
            // สลับสถานะการแสดงมือ
            ishand = true; // แสดงมือ
            handX = random.nextInt(700) + 3; // กำหนดตำแหน่งมือใหม่แบบสุ่ม
            try {
                Thread.sleep(2000); // วาดมืออยู่ 2 วินาที
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ishand = false; // ซ่อนมือ
            try {
                Thread.sleep(10000); // หายไป 10 วินาที
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

