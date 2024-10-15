import javax.swing.JPanel;

class ThreadRain extends Thread {
    PanelGame panelGame; // อ้างอิงถึง PanelGame เพื่อเข้าถึงข้อมูลของลูกอม
    tradetime tt ;
    ThreadRain(PanelGame panelGame ,   tradetime tt) {
        this.panelGame = panelGame;
        this.tt = tt;
    }

    @Override
    public void run() {
        while (tt.isend) {
            // อัปเดตตำแหน่ง Y ของลูกอม
                panelGame.donutY  += panelGame.donutSpeed;
                panelGame.pumpkinY += panelGame.pumpkinSpeed;

                if (panelGame.donutY > panelGame.getHeight() || !panelGame.isdonut) {
                    panelGame.isdonut = true;
                    panelGame.donutX =  panelGame.random.nextInt(1350)+5;
                    panelGame.donutY = 70;
                    panelGame.donutSpeed =  panelGame.random.nextDouble(6.0)+2.5;
                }
                if (panelGame.pumpkinY  > panelGame.getHeight() || !panelGame.ispumpkin) {
                    panelGame.ispumpkin = true;
                    panelGame.pumpkinX =  panelGame.random.nextInt(1350)+5;
                    panelGame.pumpkinY = 70;
                    panelGame.pumpkinSpeed =  panelGame.random.nextDouble(6.0)+2.5;
                }

            for (int i = 0; i < panelGame.Candy; i++) {
                panelGame.Rany[i] += panelGame.ranspeed[i]; // ลดตำแหน่ง Y ของลูกอม

                // ตรวจสอบว่าลูกอมตกถึงด้านล่างของหน้าจอ
                if (panelGame.Rany[i] > panelGame.getHeight() || !panelGame.iscandy[i]) {
                    panelGame.iscandy[i] = true; 
                    panelGame.Rany[i] = 70; // รีเซ็ตตำแหน่ง Y ของลูกอม
                    panelGame.Ranx[i] = panelGame.random.nextInt(1350) + 5; // รีเซ็ตตำแหน่ง X ของลูกอม
                    panelGame.ranspeed[i] = panelGame.random.nextDouble(10.0)+4.0; //สุ่มความเร็วใหม่ด้วย

                }
            }

            panelGame.repaint(); // อัปเดตหน้าจอ
            try {
                Thread.sleep(20); // หยุดชั่วคราวเพื่อให้การเคลื่อนไหวไม่เร็วเกินไป
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
