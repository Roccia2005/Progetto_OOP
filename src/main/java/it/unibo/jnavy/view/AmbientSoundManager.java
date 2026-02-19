package it.unibo.jnavy.view;

import javax.sound.sampled.*;
import javax.swing.Timer;
import java.io.IOException;
import java.net.URL;

public class AmbientSoundManager {

    private Clip clip;
    private Timer loopTimer;

    /**
     * @param soundFile Il percorso del file (es. "/sounds/sonar.wav")
     * @param intervalMs Ogni quanti millisecondi riprodurre il suono
     */
    public AmbientSoundManager(String soundFile, int intervalMs) {
        try {
            // 1. Carica il file audio in memoria (Clip)
            URL url = getClass().getResource(soundFile);
            if (url == null) {
                System.err.println("Audio file not found: " + soundFile);
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            this.clip = AudioSystem.getClip();
            this.clip.open(audioIn);

            // 2. Imposta il Timer per ripetere l'azione
            this.loopTimer = new Timer(intervalMs, e -> playSound());
            this.loopTimer.setRepeats(true);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void playSound() {
        if (clip != null) {
            // Ferma se stava ancora suonando (opzionale, per suoni brevi meglio non farlo)
            if (clip.isRunning()) {
                clip.stop();
            }
            // Riavvolgi il nastro all'inizio
            clip.setFramePosition(0);
            // Spara l'audio
            clip.start();
        }
    }

    public void start() {
        if (loopTimer != null) {
            playSound(); // Suona subito la prima volta
            loopTimer.start(); // E poi ogni tot secondi
        }
    }

    public void stop() {
        if (loopTimer != null) {
            loopTimer.stop();
        }
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}