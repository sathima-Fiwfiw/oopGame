public class showReady extends Thread {
    public volatile boolean showReadyImage = true;
    tradetime timecount;
    ThreadRain candyFallThread ;
    ThreadHand handgrost;
    
    showReady(tradetime timecount, ThreadRain candyFallThread, ThreadHand handgrost){
        this.timecount = timecount;
        this.candyFallThread = candyFallThread;
        this.handgrost = handgrost;
      
       
    }
    @Override
    public void run() {
        try {
            // แสดงภาพ "ready" เป็นเวลา 3 วินาที
            Thread.sleep(3000);
            showReadyImage = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ซ่อนภาพ "ready" หลังจาก 3 วินาที
        System.out.println("Changing showReadyImage to false");  // Debugging output
        showReadyImage = false;

        // เริ่มเกมเมื่อ showReadyImage เปลี่ยนเป็น false
        startGame();
    }

    private void startGame() {
        if (!showReadyImage) {
            System.out.println("Starting game...");
            candyFallThread.start();
            timecount.startdown();
            handgrost.start();
        }
    }
}
