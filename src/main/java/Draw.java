import javax.swing.*;
import java.awt.*;

public class Draw {
    public static void main(String[] args) {
        JFrame frame = new JFrame("绘制矩形");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 1000);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, 1000, 10);
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }
}