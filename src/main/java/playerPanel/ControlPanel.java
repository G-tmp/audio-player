package playerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class ControlPanel extends JPanel {
    //    private PausablePlayer player;
    static ImageIcon playIcon;
    static ImageIcon pauseIcon;
    static ImageIcon selectIcon;
    JButton selectButton;
    JButton playButton;
    JFileChooser chooser;

    private static String HOME = System.getProperty("user.home");

//    static {
//
//    }


    public ControlPanel() {
        JPanel panel = new JPanel();

//        final String internalImagePath = "res/images/animation.gif";
//        //example one
//        final Image playImage = Toolkit.getDefaultToolkit().createImage(this.getClass().getClassLoader().getResource("resources/images/Play.gif"));
        //example two
//        Toolkit.getDefaultToolkit().createImage(ControlPanel.class.getResource(internalImagePath));

        try {
            playIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/Play.gif")));
            pauseIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/Pause.png")));
            selectIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/Open.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }


        selectButton = new JButton(selectIcon);
        playButton = new JButton(playIcon);

        chooser = new JFileChooser(HOME + "/Music/");

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        playButton.setContentAreaFilled(false);
        chooser.setToolTipText("Choose a music");
        playButton.setToolTipText("Resume or pause the playback");

        add(selectButton);
        add(playButton);
//        add(panel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new ControlPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(400, 200);
    }
}