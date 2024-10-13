import java.awt.BorderLayout;
import java.awt.Label;
import javax.swing.JFrame;

public class tradetime {
    public static void main(String[] args) {
        MyThread myThread = new MyThread(3, 00);
        myThread.setVisible(true);
    }
}

class MyThread extends JFrame {
    Label ltime = new Label("00:00");
    private int minutes;
    private int seconds;

    public MyThread(int minutes, int seconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(ltime, BorderLayout.CENTER);

        startdown();
    }

    public void startdown() {
        new Thread(() -> {
            try {
                while (minutes > 0 || seconds > 0) {
                    ltime.setText(String.format("%02d:%02d", minutes, seconds));
                    Thread.sleep(1000);

                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }
                }
                ltime.setText("Time's up!");
            } catch (InterruptedException e) {
                ltime.setText("Countdown interrupted");
            }
        }).start();
    }

}
