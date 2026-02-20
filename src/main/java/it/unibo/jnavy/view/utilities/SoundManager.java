package it.unibo.jnavy.view.utilities;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Manages audio playback for both continuous background music and one-shot sound effects.
 */
public class SoundManager {

    private Clip ambientClip;

    /**
     * Constructs a new manager for a specific continuous background sound.
     * @param soundFile the relative path to the audio resource file.
     */
    public SoundManager(String soundFile) {
        try {
            URL url = getClass().getResource(soundFile);
            if (url == null) {
                System.err.println("Sound file not found: " + soundFile);
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            this.ambientClip = AudioSystem.getClip();
            this.ambientClip.open(audioIn);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the playback of the ambient audio in a continuous loop.
     */
    public void start() {
        if (ambientClip != null) {
            ambientClip.setFramePosition(0); 
            ambientClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Pauses the audio playback without releasing resources.
     */
    public void stop() {
        if (ambientClip != null && ambientClip.isRunning()) {
            ambientClip.stop();
        }
    }

    /**
     * Stops the audio and fully releases the system resources.
     */
    public void close() {
        if (ambientClip != null) {
            ambientClip.stop();
            ambientClip.flush();
            ambientClip.close();
        }
    }

    /**
     * Plays a sound effect once and automatically releases the memory when finished.
     * @param filePath the relative path to the audio resource file.
     */
    public static void playOneShotSound(String filePath) {
        new Thread(() -> {
            try {
                URL soundUrl = SoundManager.class.getResource(filePath);
                if (soundUrl != null) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundUrl);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}