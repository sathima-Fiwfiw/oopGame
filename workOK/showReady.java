public class showReady extends Thread {
    public volatile boolean showReadyImage = true; // เปลี่ยนค่าเริ่มต้นเป็น true

    @Override
    public void run() {
        try {
            // แสดงภาพ "ready" เป็นเวลา 3 วินาที
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ซ่อนภาพ "ready" หลังจาก 3 วินาที
        System.out.println("Changing showReadyImage to false");  // Debugging output
        showReadyImage = false; // เปลี่ยนเป็น false
    }
}