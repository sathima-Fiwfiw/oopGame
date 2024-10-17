import javax.swing.JPanel;
import java.util.Random;

class ThreadRain extends Thread {
    tradetime tt ;
    Random random = new Random();
    int Candy=2;   
    int[] Ranx =new int[Candy];
    int[] Rany =new int[Candy];
    double[] ranspeed=new  double[Candy];
    boolean[] iscandy = new boolean[Candy];
    boolean isdonut = true;
    boolean ispumpkin= true;
    int donutWidth = 50, donutHeight = 50;
    int donutX,donutY = 70;
    double donutSpeed;

    int pumpkinX,pumpkinY = 70;
    double pumpkinSpeed;

    ThreadRain(tradetime tt) {
        this.tt = tt;

        for (int i = 0; i < Candy; i++) {
               Ranx[i] =random.nextInt(1350)+5;
               Rany[i] = 70;
               ranspeed[i] = random.nextDouble(10.0)+4.0;
        }
           donutX =random.nextInt(1350)+5;
           donutY = 70;
           donutSpeed = random.nextDouble(6.0)+2.5;
    
           pumpkinX =  random.nextInt(1350)+5;
           pumpkinY = 70;
           pumpkinSpeed = random.nextDouble(6.0)+2.5;
    }

    @Override
    public void run() {
        while (tt.isend) {
            // อัปเดตตำแหน่ง Y ของลูกอม
                donutY  += donutSpeed;
                pumpkinY += pumpkinSpeed;
                if (donutY > 840|| !isdonut) {
                    isdonut = true;
                    donutX =  random.nextInt(1350)+5;
                    donutY = 70;
                    donutSpeed =  random.nextDouble(6.0)+2.5;
                }
                if (pumpkinY  > 840 || !ispumpkin) {
                    ispumpkin = true;
                    pumpkinX =  random.nextInt(1350)+5;
                    pumpkinY = 70;
                    pumpkinSpeed =  random.nextDouble(6.0)+2.5;
                }

            for (int i = 0; i < Candy; i++) {
                Rany[i] += ranspeed[i]; // ลดตำแหน่ง Y ของลูกอม

                // ตรวจสอบว่าลูกอมตกถึงด้านล่างของหน้าจอ
                if (Rany[i] > 840 || !iscandy[i]) {
                    iscandy[i] = true; 
                    Rany[i] = 70; // รีเซ็ตตำแหน่ง Y ของลูกอม
                    Ranx[i] = random.nextInt(1350) + 5; // รีเซ็ตตำแหน่ง X ของลูกอม
                    ranspeed[i] = random.nextDouble(10.0)+4.0; //สุ่มความเร็วใหม่ด้วย

                }
            }
            try {
                Thread.sleep(20); // หยุดชั่วคราวเพื่อให้การเคลื่อนไหวไม่เร็วเกินไป
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
