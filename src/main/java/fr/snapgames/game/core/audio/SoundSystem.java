package fr.snapgames.game.core.audio;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.resources.ResourceManager;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is intended to manage and control Sound play and output.
 *
 * @author Frédéric Delorme.
 */
public class SoundSystem {

    private Game game;
    /**
     * Max number of SoundClip to be stored in cache.
     */
    private static final int MAX_SOUNDS_IN_STACK = 40;

    /**
     * Internal play Stack
     */
    private Stack<String> soundsStack = new Stack<String>();
    /**
     * Internal SoundBank.
     */
    private Map<String, SoundClip> soundBank = new ConcurrentHashMap<String, SoundClip>();

    /**
     * mute the full soudsystem
     */
    private boolean mute = false;

    /**
     * Internal constructor.
     */
    public SoundSystem(Game game) {
        this.game = game;
        initialize(game);
    }

    /**
     * Load a Sound from <code>filename</code> to the sound bank.
     *
     * @param filename file name of the sound to be loaded to the
     *                 <code>soundBank</code>.
     * @return filename if file has been loaded into the sound bank or null.
     * {@link SoundSystem}
     * <p>
     * SoundClip coinClip =
     * (SoundClip)ResourceManager.get("res/audio/sounds/135936__bradwesson__collectcoin.wav");
     * SystemManager.get(SoundSystem.class).add("coin", coinClip);
     * </p>
     * .
     */
    public String load(String code, String filename) {
        if (!soundBank.containsKey(code) && !mute) {
            SoundClip sc = ResourceManager.getSoundClip(filename);
            if (sc != null) {
                soundBank.put(code, sc);
                System.out.printf("Load sound %s to sound bank with code %s%n", filename, code);
            }
            return filename;
        } else {
            return null;
        }
    }

    /**
     * Add the {@link SoundClip} to the sounds bank withe <code>code</code> as
     * identifier.
     *
     * @param code the code to identify the SoundClip in the bank
     * @param sc   the {@link SoundClip} top be added.
     * @return
     */
    public String add(String code, SoundClip sc) {
        if (sc != null && !mute) {
            soundBank.put(code, sc);
            System.out.printf("Load sound %s to Sound Bank.%n", sc.getCode(), code);
        }
        return code;
    }

    /**
     * play the sound with <code>code</code>
     *
     * @param code internal code of the sound to be played.
     */
    public void play(String code) {
        if (!mute) {
            if (soundBank.containsKey(code)) {
                SoundClip sc = soundBank.get(code);
                sc.play();
                System.out.printf("INFO: Play sound %s%n", code);
            } else {
                System.err.printf("ERROR: Unable to find the sound %s in the SoundBank !%n", code);
            }
        } else {
            System.out.printf("INFO: Mute mode activated, %s not played%n", code);
        }
    }

    /**
     * play the sound with <code>code</code>
     *
     * @param code   internal code of the sound to be played.
     * @param volume volume level to be played.
     */
    public void play(String code, float volume) {
        if (!mute) {
            if (soundBank.containsKey(code)) {
                SoundClip sc = soundBank.get(code);
                float soundVolume = ((game != null &&
                        game.getConfiguration() != null) ?
                        game.getConfiguration().getFloat("game.sound.volume", 1.0f) : 1.0f);
                sc.play(0.5f, volume * soundVolume);
                System.out.printf("INFO: Play sound %s with volume %s%n", code, volume);
            } else {
                System.err.printf("unable to find the sound %s in the SoundBank !%n", code);
            }
        } else {
            System.out.printf("Mute mode activated, %s not played%n", code);
        }
    }

    /**
     * play the sound with <code>code</code>
     *
     * @param code   internal code of the sound to be played.
     * @param volume volume level to be played.
     * @param pan    the pan for the sound to be played.
     */

    public void play(String code, float volume, float pan, boolean loop) {
        if (!mute) {

            if (soundBank.containsKey(code)) {
                SoundClip sc = soundBank.get(code);
                if (loop && sc != null) {
                    sc.play(pan, volume);
                    sc.loop();
                }
                System.out.printf("INFO: Play sound %s with volume %s and pan %s%n", code, volume, pan);
            } else {
                System.err.printf("ERROR: unable to find the sound %s in the SoundBank !%n", code);
            }
        } else {
            System.out.printf("INFO: Mute mode activated, %s not played%n", code);
        }
    }

    public void play(String code, float volume, float pan) {
        play(code, volume, pan, false);
    }

    public void loop(String code, float volume) {
        play(code, volume, 0.5f, true);
    }

    /**
     * Is the sound code playing right now ?
     *
     * @param code code of the sound to test.
     * @return
     */
    public boolean isPlaying(String code) {
        if (soundBank.containsKey(code)) {
            return soundBank.get(code).isPlaying();
        } else {
            return false;
        }
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    public int initialize(Game game) {
        this.game = game;
        int maxSoundStack = game.getConfiguration().getInteger("game.sound.max.sample", MAX_SOUNDS_IN_STACK);
        soundsStack.setSize(maxSoundStack);
        System.out.printf("INFO: Initialize SoundControl with %s stack places%n", MAX_SOUNDS_IN_STACK);
        Type[] supportedFiletypes = AudioSystem.getAudioFileTypes();
        for (Type t : supportedFiletypes) {
            System.out.printf("INFO: supported file format '%s'%n", t);
        }
        this.mute = game.getConfiguration().getBoolean("game.sound.mute", false);
        Mixer.Info[] infos = AudioSystem.getMixerInfo();
        for (Info info : infos) {
            System.out.printf("INFO: Mixer info: %s%n", info);
        }
        return 0;
    }

    public void dispose() {
        soundBank.clear();
    }

    public void stopAll() {
        soundBank.values().forEach(sb -> sb.stop());
    }
}