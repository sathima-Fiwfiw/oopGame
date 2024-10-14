
public class tradetime {
    int minutes;
    int seconds;
    tradetime(int minutes, int seconds){
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public void startdown() {
        new Thread(() -> {
            try {
                while (minutes > 0 || seconds > 0) {
                   // ltime.setText(String.format("%02d:%02d", minutes, seconds));
                    Thread.sleep(1000);

                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                }
               // ltime.setText("Time's up!");
            } catch (InterruptedException e) {
                //ltime.setText("Countdown interrupted");
            }
        }).start();
    }

}
