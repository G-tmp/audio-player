import playerPanel.HybirdFrame;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            HybirdFrame frame = new HybirdFrame();
            frame.setTitle("player");
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 220);
        });
    }
}