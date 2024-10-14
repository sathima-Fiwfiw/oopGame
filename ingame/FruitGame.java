import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class FruitGame extends JFrame {
    private int score = 0; // คะแนนเริ่มต้น
    private JLabel scoreLabel;
    private Random random = new Random();
    private JLabel target; // เป้าเล็ง

    public FruitGame() {
        // ตั้งค่าหน้าต่างเกม
        setTitle("Fruit Collector Game");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // ใช้ layout เป็น null เพื่อจัดการตำแหน่งเอง

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setBounds(150, 20, 200, 30);
        add(scoreLabel);

        // เริ่ม Timer เพื่อสร้างผลไม้ทุกๆ 1 วินาที
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnFruit();
            }
        });
        timer.start();

        // สร้างเป้าเล็ง
        target = new JLabel();
        target.setOpaque(true);
        target.setBackground(Color.YELLOW);
        target.setBounds(0, 0, 30, 30); // ขนาดของเป้าเล็ง
        add(target);

        // ตั้งค่าการเคลื่อนที่ของเป้าเล็ง
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                target.setLocation(e.getX() - target.getWidth() / 2, e.getY() - target.getHeight() / 2);
            }
        });

        setVisible(true);
    }

    private void spawnFruit() {
        // สุ่มประเภทของผลไม้ (ผลไม้ดีและผลไม้เน่า)
        Fruit fruit = random.nextBoolean() ? new Fruit("Apple", 10) : new Fruit("Rotten Banana", -5);
        JLabel fruitLabel = new JLabel(fruit.getName());
        fruitLabel.setForeground(fruit.getPointValue() > 0 ? Color.GREEN : Color.RED);
        fruitLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        fruitLabel.setBounds(random.nextInt(350), 0, 100, 30); // ตั้งค่าตำแหน่งสุ่ม
        add(fruitLabel);
        fruitLabel.setVisible(true);

        // ทำให้ผลไม้ตกลงมา
        Timer fallTimer = new Timer(100, new ActionListener() {
            private int fallDistance = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                fallDistance += 5;
                fruitLabel.setLocation(fruitLabel.getX(), fruitLabel.getY() + 5);

                if (fallDistance > 600) { // หากตกถึงพื้น
                    ((Timer) e.getSource()).stop(); // หยุด Timer
                    remove(fruitLabel); // ลบผลไม้ที่ตกถึงพื้น
                } else {
                    checkCollision(fruitLabel, fruit); // ตรวจสอบการชน
                }
                repaint(); // วาดใหม่ทุกครั้งที่มีการตก
            }
        });
        fallTimer.start();
    }

    private void checkCollision(JLabel fruitLabel, Fruit fruit) {
        // ตรวจสอบการชนระหว่างเป้าเล็งกับผลไม้
        if (fruitLabel.isVisible() && target.getBounds().intersects(fruitLabel.getBounds())) {
            updateScore(fruit); // เพิ่มคะแนนเมื่อชน
            remove(fruitLabel); // ลบผลไม้ที่ชน
            repaint(); // วาดใหม่หลังจากลบผลไม้
        }
    }

    private void updateScore(Fruit fruit) {
        score += fruit.getPointValue(); // เพิ่มคะแนน
        scoreLabel.setText("Score: " + score); // อัปเดตคะแนนบนหน้าจอ
    }

    public static void main(String[] args) {
        new FruitGame();
    }
}

class Fruit {
    private String name;
    private int pointValue;

    public Fruit(String name, int pointValue) {
        this.name = name;
        this.pointValue = pointValue;
    }

    public String getName() {
        return name;
    }

    public int getPointValue() {
        return pointValue;
    }
}
