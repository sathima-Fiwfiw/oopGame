
public class tradetime {
    int minutes;
    int seconds;
    
    boolean isshowtime = false;
    boolean isend = true;
    tradetime(int minutes, int seconds){
        this.minutes = minutes;
        this.seconds = seconds;
        
    }

    public void startdown() {
        new Thread(() -> {
            try {
                while (minutes > 0 || seconds > 0) {
                    // รอจนกว่า showReadyImage จะเป็น false เพื่อเริ่มนับถอยหลัง

                        Thread.sleep(1000);

                        if (seconds == 0) {
                            minutes--;
                            seconds = 59;
                        } else {
                            seconds--;
                        }
                    }

                isend = false;
                
               // ltime.setText("Time's up!");
            } catch (InterruptedException e) {
                //ltime.setText("Countdown interrupted");
            }
        }).start();
    }

}
