package playerPanel;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel authorLabel;


    public InfoPanel() {
        titleLabel = new JLabel();
        authorLabel = new JLabel();

        titleLabel.setForeground(Color.BLACK);
        authorLabel.setForeground(Color.GRAY);
        titleLabel.setFont(new Font(null, Font.BOLD, 22));
        authorLabel.setFont(new Font(null, Font.ITALIC, 20));
        setLayout(new FlowLayout(10, 20, 20));
        add(titleLabel);
        add(authorLabel);
    }


    public void setTitle(String title) {
        if (title == null)
            titleLabel.setText("Unknow");
        else
            titleLabel.setText(title);
    }


    public void setAuthor(String author) {
        if (author == null)
            authorLabel.setText("Unknow");
        else
            authorLabel.setText(author);
    }

}