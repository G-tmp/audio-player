package playback;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.Map;

/**
 * JLayer - Pause and resume song
 * https://stackoverflow.com/questions/12057214/jlayer-pause-and-resume-song
 */
public class PausablePlayer {
    // music play status
    public  enum PlayStatus {
        NOTSTARTED(0, "not started"),
        PLAYING(1, "playing"),
        PAUSED(2, "paused"),
        FINISHED(3, "finished");

        private int code;
        private String status;

        PlayStatus(int code, String status) {
            this.code = code;
            this.status = status;
        }
    }


    private volatile PlayStatus playerStatus;
    private Player player;
    private InputStream is;
    private long totalBytes;
    private RandomAccessFile raf;
    private File file;


    public PausablePlayer(String path) throws IOException, JavaLayerException {
        this(new File(path));
    }

    public PausablePlayer(File file) throws JavaLayerException, IOException {
        this(file, null);
    }

    public PausablePlayer(File file, AudioDevice audioDevice) throws JavaLayerException, IOException {
        this.file = file;
        raf = new RandomAccessFile(file, "r");
        is = Channels.newInputStream(raf.getChannel());
        totalBytes = raf.length();
        player = new Player(is, audioDevice);
        playerStatus = PlayStatus.NOTSTARTED;
    }


//    public long getPos() throws IOException {
//        return raf.getFilePointer();
//    }


    public void skip(float percent) {
        skip((long) (percent * totalBytes));
    }


    public void skip(long pos) {
        if (pos > totalBytes) {
            throw new IllegalArgumentException("over max byte");
        }

        synchronized (this) {
            System.out.println("skip");
            try {
                raf.seek(pos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<String, Object> getProperties() {
        AudioFileFormat fileFormat = null;
        Map<String, Object> properties = null;

        try {
            fileFormat = AudioSystem.getAudioFileFormat(this.file);

            if (fileFormat instanceof TAudioFileFormat) {
                properties = ((TAudioFileFormat) fileFormat).properties();
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }


    public void choose(String path) throws IOException, JavaLayerException {
        choose(new File(path));
    }


    public void choose(File file) throws IOException, JavaLayerException {
        this.file = file;
        raf = new RandomAccessFile(file, "r");
        is = Channels.newInputStream(raf.getChannel());
        player = new Player(is);
        totalBytes = raf.length();
//        player = new Player(is, audioDevice);
        playerStatus = PlayStatus.NOTSTARTED;
    }


    public void play() {
        synchronized (this) {
            switch (playerStatus) {
                // start the player
                case NOTSTARTED:
                    Runnable r = () -> {
                        playInternal();
                    };
                    new Thread(r).start();

                    playerStatus = PlayStatus.PLAYING;
                    break;
                // pause
                case PAUSED:
                    resume();
                    break;
                case PLAYING:
                    break;
                case FINISHED:
                    break;
                default:
                    break;
            }
        }
    }


    public PlayStatus getPlayerStatus() {
        return playerStatus;
    }


    private void playInternal() {
        // playerStatus set FINISHED , thread finished
        while (playerStatus != PlayStatus.FINISHED) {
            // check if paused or terminated
            synchronized (this) {
                while (playerStatus == PlayStatus.PAUSED) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // return true if the last frame was played,
            // or false if there are more frames.
            try {
                // play one frame each time
                if (!player.play(1))
                    break;
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }

        fclose();
    }


    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause() {
        synchronized (this) {
            System.out.println("pause");
            if (playerStatus == PlayStatus.PLAYING) {
                playerStatus = PlayStatus.PAUSED;
            }
            return playerStatus == PlayStatus.PAUSED;
        }
    }


    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    public boolean resume() {
        synchronized (this) {
            System.out.println("resume");
            if (playerStatus == PlayStatus.PAUSED) {
                playerStatus = PlayStatus.PLAYING;
                this.notifyAll();
            }
            return playerStatus == PlayStatus.PAUSED;
        }
    }


    /**
     * Stops playback. If not playing, does nothing
     */
    public void stop() {
        synchronized (this) {
            System.out.println("stop");
            playerStatus = PlayStatus.FINISHED;
            this.notifyAll();
        }
    }


    /**
     * Closes the player, regardless of current state.
     */
    public void fclose() {
        synchronized (this) {
            playerStatus = PlayStatus.FINISHED;
        }

        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        player.close();

        System.out.println("fin");
    }

}
