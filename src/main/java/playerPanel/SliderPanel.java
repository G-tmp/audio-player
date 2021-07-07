package playerPanel;

import javax.swing.*;
import java.awt.*;

/**
 *
 *  contains slider control and display label
 *  be enable to move and pause
 *  drag slider change player stream
 *
 */
public class SliderPanel extends JPanel {
    private static final int NOTSTARTED = 0;
    private static final int MOVING = 1;
    private static final int PAUSED = 2;
    private static final int FINISHED = 3;
    private volatile int status = NOTSTARTED;
    private volatile int pos;
    private volatile float percent;
    private int duration;

    JSlider slider;          // slider is able to drag
    private JLabel progressLabel;   // display time progress



    public SliderPanel() {
        this(0);
    }


    public SliderPanel(int duration) {
        setLayout(new BorderLayout());
        this.pos = 0;
        this.percent = 0F;
        this.duration = duration;
        slider = new JSlider();
        progressLabel = new JLabel();

        progressLabel.setFont(new Font(null, Font.PLAIN, 18));
        slider.setValue(0);
        slider.setMaximum(this.duration);
        progressLabel.setText(secondsTransfer(slider.getValue()) + " / " + secondsTransfer(this.duration) + "  ");

        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            progressLabel.setText(secondsTransfer(slider.getValue()) + " / " + secondsTransfer(this.duration) + "  ");
        });
//        slider.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                //  release
//                float percent = (float) slider.getValue() / slider.getMaximum();
//                setPosition(percent);
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                //  press
//                float percent = (float) slider.getValue() / slider.getMaximum();
//                setPosition(percent);
//            }
//        });

//        JPanel panel = new JPanel();
//        panel.add(this.slider);
//        panel.add(this.progressLabel);
//        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor = GridBagConstraints.WEST;
//        add(panel, gbc);
        add(this.slider, BorderLayout.CENTER);
        add(this.progressLabel, BorderLayout.EAST);
    }


    // transfer time format
    private String secondsTransfer(int sec) {
        int minute = 0;
        int second = 0;
        StringBuilder result = new StringBuilder();

        minute = sec / 60;
        second = sec % 60;

        if (minute < 10)
            result.append("0").append(minute);
        else
            result.append(minute);
        result.append(":");
        if (second < 10)
            result.append("0").append(second);
        else
            result.append(second);

        return String.valueOf(result);
    }


    public  void setDuration(int duration) {
        synchronized (this) {
            if (status == MOVING) {
                this.status = PAUSED;
            }
            this.duration = duration;
            this.pos = 0;
            slider.setValue(0);
            slider.setMaximum(this.duration);
            progressLabel.setText(secondsTransfer(slider.getValue()) + " / " + secondsTransfer(duration) + "  ");
//        repaint();
        }
    }


    public synchronized float setPosition(float percent) {
        System.out.println(percent);
        this.percent = percent;
        pos = (int) (percent * duration);
        setPosition((int) (percent * duration));
        return percent;
    }


    private synchronized int setPosition(int n) {
//        if (pos == duration) {
//            status = FINISHED;
//        }

        if (n < 0) {
            throw new IllegalArgumentException("parament between 0 to duration");
        }

        slider.setValue(n);
        repaint();
        return this.pos;
    }


    public float getPercent() {
        return percent;
    }


    // click play button
    public void start() {
        synchronized (this) {
            switch (status) {
                case NOTSTARTED:
                    Runnable r = () -> {
                        moveInternal();
                    };
                    new Thread(r).start();
                    status = MOVING;
                    break;
                case PAUSED:
                    resume();
                    break;
                case MOVING:
                    break;
                default:
                    break;
            }
        }
    }


    private void moveInternal() {
        while (status != FINISHED) {
            synchronized (this) {
                while (status == PAUSED) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (pos >= duration){
                    status = PAUSED;
                }
            }

            try {
                setPosition(pos++);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void pause() {
        synchronized (this) {
            if (status == MOVING)
                status = PAUSED;
        }
    }


    public void resume() {
        synchronized (this) {
            if (status == PAUSED) {
                status = MOVING;
                this.notifyAll();
            }
        }
    }

}