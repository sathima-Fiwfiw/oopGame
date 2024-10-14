import javax.swing.JPanel;

class ThreadRain extends Thread {
    private final PanelGame panelGame; // อ้างอิงถึง PanelGame เพื่อเข้าถึงข้อมูลของลูกอม

    ThreadRain(PanelGame panelGame) {
        this.panelGame = panelGame;
    }

    @Override
    public void run() {
        while (true) {
            // อัปเดตตำแหน่ง Y ของลูกอม
            for (int i = 0; i < panelGame.Candy; i++) {
                panelGame.Rany[i] += panelGame.ranspeed[i]; // ลดตำแหน่ง Y ของลูกอม

                // ตรวจสอบว่าลูกอมตกถึงด้านล่างของหน้าจอ
                if (panelGame.Rany[i] > panelGame.getHeight()) {
                    panelGame.Rany[i] = -panelGame.random.nextInt(100); // รีเซ็ตตำแหน่ง Y ของลูกอม
                    panelGame.Ranx[i] = panelGame.random.nextInt(1200) + 5; // รีเซ็ตตำแหน่ง X ของลูกอม
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
