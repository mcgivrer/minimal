package fr.snapgames.game.core.audio;


import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to play and manage a sound clip from file.
 *
 * @author Frédéric Delorme
 */
public class SoundClip {

    private static int soundIndex = 0;

    private String code = "SOUND_" + (soundIndex++);
    /**
     * Java Sound clip to be read.
     */
    private Clip clip;
    /**
     * Volume control.
     */
    private FloatControl gainControl;
    /**
     * Pan Control.
     */
    private FloatControl panControl;
    /**
     * Balance Control.
     */
    private FloatControl balanceControl;

    public SoundClip(String code, InputStream is) {
        this.code = code;
        try {
            loadFromStream(code, is);
        } catch (Exception e) {
            System.err.printf("ERROR: Unable to load sound file %s : %s%n", code, e.getMessage());
        }
    }

    /**
     * Initialize the sound clip ready to play from the file at <code>path</code>.
     *
     * @param path Path to the sound clip to be read.
     */
    public SoundClip(String path) {
        try {
            InputStream audioSrc = SoundClip.class.getResourceAsStream("/" + path);
            if (audioSrc == null) {
                System.err.printf("ERROR: Unable to read the sound file %s%n", path);
            } else {
                loadFromStream(path, audioSrc);
            }
        } catch (Exception e) {
            System.err.printf("ERROR: Unable to play the sound file %s : %s%n", path, e.getMessage());
        }

    }

    private void loadFromStream(String path, InputStream audioSrc)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        AudioFormat baseFormat = ais.getFormat();
        System.out.printf("INFO: SoundClip '%s' with Base format: [%s].%n", path, baseFormat);

        AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
                baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
        AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
        clip = AudioSystem.getClip();
        /*
         * bugfix proposed
         *
         * @see
         * https://stackoverflow.com/questions/5808560/error-playing-sound-java-no-line-
         * matching-interface-clip-supporting-format#answer-41647865
         */
        clip.addLineListener(event -> {
            if (LineEvent.Type.STOP.equals(event.getType())) {
                clip.close();
            }
        });
        if (!clip.isActive() && !clip.isRunning()) {
            clip.open(dais);
        }

        if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
            balanceControl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
        } else {
            System.out.printf("INFO: BALANCE control is not supported%n");
        }

        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } else {
            System.out.printf("INFO: MASTER_GAIN control is not supported%n");
        }
        if (clip.isControlSupported(FloatControl.Type.PAN)) {
            panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
        } else {
            System.out.printf("INFO: PAN control is not supported%n");
        }
    }

    /**
     * Start playing the clip.
     */
    public SoundClip play() {
        if (clip == null) {
            return this;
        } else {
            try {
                clip.flush();
                clip.stop();
                clip.drain();
                clip.setMicrosecondPosition(0);
                clip.start();
            } catch (RuntimeException e) {
                System.err.printf("ERROR: Unable to play sound on %s%n", clip.getLineInfo());
            }
        }
        return this;
    }

    public SoundClip play(float pan, float volume) {
        setPan(pan);
        setVolume(volume);
        play();
        return this;
    }

    public SoundClip play(float pan, float volume, float balance) {
        setPan(pan);
        setBalance(balance);
        setVolume(volume);
        play();
        return this;
    }

    /**
     * Set balance for this sound clip.
     *
     * @param balance
     */
    private SoundClip setBalance(float balance) {

        if (balanceControl != null) {
            balanceControl.setValue(balance);
        }
        return this;
    }

    /**
     * Set Panning for this sound clip.
     *
     * @param pan
     */
    public SoundClip setPan(float pan) {
        if (panControl != null) {
            panControl.setValue(pan);
        }
        return this;
    }

    /**
     * Set Volume for this sound clip.
     *
     * @param volume
     */
    public SoundClip setVolume(float volume) {
        if (gainControl != null) {
            float min = gainControl.getMinimum() / 4;
            if (volume != 1) {
                gainControl.setValue(min * (1 - volume));
            }
        }
        return this;
    }

    /**
     * Stop playing the clip.
     */
    public SoundClip stop() {
        if (clip == null) {
            return this;
        } else if (clip.isRunning()) {
            clip.stop();
        }
        return this;
    }

    /**
     * Loop the clip continuously
     */
    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            while (!clip.isRunning()) {
                clip.start();
            }
        }
    }

    /**
     * Close the clip.
     */
    public void close() {
        stop();
        clip.drain();
        clip.close();
    }

    /**
     * is the clip is playing ?
     *
     * @return
     */
    public boolean isPlaying() {
        return clip.isRunning();
    }

    /**
     * return the Code for this soundclip.
     *
     * @return
     */
    public Object getCode() {
        return this.code;
    }

}