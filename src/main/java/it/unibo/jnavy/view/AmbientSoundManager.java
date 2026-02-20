package it.unibo.jnavy.view;

import javax.sound.sampled.*;
import javax.swing.Timer;
import java.io.IOException;
import java.net.URL;

/**
 * Manages the periodic playback of an ambient audio file.
 * 
 * This class uses a {@link javax.swing.Timer} to repeatedly trigger a sound 
 * at a specified interval. It is designed to handle background noises 
 * or environmental effects.
 */
public class AmbientSoundManager {

    private Clip clip;
    private Timer loopTimer;

    /**
     * Constructs a new manager for the specified sound file.
     *
     * The audio file is loaded into memory upon instantiation to ensure 
     * zero latency during playback. Ensure the file is in a supported 
     * format.
     *
     * @param soundFile  the relative path to the audio resource file.
     * @param intervalMs the time interval, in milliseconds, between each playback trigger.
     */
    public AmbientSoundManager(String soundFile, int intervalMs) {
        try {
            URL url = getClass().getResource(soundFile);
            if (url == null) {
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            this.clip = AudioSystem.getClip();
            this.clip.open(audioIn);

            this.loopTimer = new Timer(intervalMs, e -> playSound());
            this.loopTimer.setRepeats(true);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (loopTimer != null) {
            playSound();
            loopTimer.start();
        }
    }

    public void stop() {
        if (loopTimer != null) {
            loopTimer.stop();
        }
        if (clip != null) {
            clip.stop();
            clip.flush();
            clip.close();
        }
    }

    private void playSound() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }
}