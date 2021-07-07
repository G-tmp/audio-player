package playerPanel;

import javazoom.jl.decoder.JavaLayerException;
import playback.PausablePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class HybirdFrame extends JFrame {
    private PausablePlayer player;
    private ControlPanel control;
    private SliderPanel slider;
    private InfoPanel info;


    public HybirdFrame() {
        control = new ControlPanel();
        slider = new SliderPanel();
        info = new InfoPanel();
        setLayout(new GridLayout(2, 1));

        control.selectButton.addActionListener(event -> {
            int returnValue = control.chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                if (player != null)
                    player.fclose();
                File selectedFile = control.chooser.getSelectedFile();
                chooseMusic(selectedFile);
                control.playButton.setIcon(control.playIcon);

                Map<String, Object> properties = player.getProperties();
                long duration = (long) properties.get("duration");
                slider.setDuration((int) (duration / 1000 / 1000));
//                System.out.println(properties);
                info.setTitle((String) properties.get("title"));
                info.setAuthor((String) properties.get("author"));
            }
        });

        control.playButton.addActionListener(event -> {
            if (player == null)
                return;

            PausablePlayer.PlayStatus status = player.getPlayerStatus();
            if (status == PausablePlayer.PlayStatus.NOTSTARTED || status == PausablePlayer.PlayStatus.PAUSED) {
                player.play();
                slider.start();
                control.playButton.setIcon(control.pauseIcon);
            } else if (status == PausablePlayer.PlayStatus.PLAYING) {
                player.pause();
                slider.pause();
                control.playButton.setIcon(control.playIcon);
            }
        });

        slider.slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //  release
                float percent = (float) slider.slider.getValue() / slider.slider.getMaximum();
                slider.setPosition(percent);
                player.skip(slider.getPercent());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //  press
                float percent = (float) slider.slider.getValue() / slider.slider.getMaximum();
                slider.setPosition(percent);
                player.skip(slider.getPercent());
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(control, "South");
        panel.add(slider);

        add(info);
        add(panel);

//        info.setBackground(Color.BLUE);
//        slider.setBackground(Color.yellow);
//        control.setBackground(Color.red);
    }


    private void chooseMusic(File file) {
        try {
            player = new PausablePlayer(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }


}