package fr.snapgames.game.core.resources;

import fr.snapgames.game.core.audio.SoundClip;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * {@link ResourceManager} is a static library providing utility to load and cache resources like images.
 * <p>
 * Usage:
 * <p>
 * A very simple approach consists in caching the read object, ad pull them from cache when already existing.
 * <ol>
 *     <li>Load Image resource : {@link ResourceManager#getImage(String)} :
 * <pre>
 *     BufferedImage img = ResourceManager.loadImage("/path/to/my/image.png");
 * </pre>
 * The image file <code>/path/to/my/image.png</code> is first loaded, added to cache and then returned as a
 * {@link BufferedImage} to caller.
 *
 *     </li>
 * </ol>
 *
 * <blockquote><em><strong>NOTE</strong> A Future enhancement will consists in adding a plugin architecture
 * to add new type file support to the {@link ResourceManager}.</em></blockquote>
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class ResourceManager {

    private static Map<String, Object> resources = new HashMap<>();

    /**
     * Request for an image. If {@link BufferedImage} already exists in cache, pull it fom.
     *
     * @param filePath the path to the image to be loaded.
     * @return a BufferedImage instance corresponding to the loaded image. If file not found, return null.
     */
    public static BufferedImage getImage(String filePath) {
        if (!addResource(filePath)) return null;
        return (BufferedImage) resources.get(filePath);
    }

    /**
     * Request for an {@link SoundClip}. If {@link SoundClip} already exists in cache, pull it fom.
     *
     * @param filePath the path to the image to be loaded.
     * @return a BufferedImage instance corresponding to the loaded image. If file not found, return null.
     */
    public static SoundClip getSoundClip(String filePath) {
        if (!addResource(filePath)) return null;
        return (SoundClip) resources.get(filePath);
    }

    /**
     * Request for an {@link Font}. If {@link Font} already exists in cache, pull it fom.
     *
     * @param filePath the path to the image to be loaded.
     * @return a BufferedImage instance corresponding to the loaded image. If file not found, return null.
     */
    public static Font getFont(String filePath) {
        if (!addResource(filePath)) return null;
        return (Font) resources.get(filePath);
    }


    /**
     * Add a resource to the internal cache.
     *
     * @param filePath the cache filename identifier
     * @return a boolean status set to true if resource is added, else false.
     */
    private static boolean addResource(String filePath) {
        switch (filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase()) {
            // read Image.
            case "PNG", "JPG" -> {
                BufferedImage img = null;
                if (!resources.containsKey(filePath)) {
                    try {
                        img = ImageIO.read(ResourceManager.class.getResourceAsStream(filePath));
                        resources.put(filePath, img);
                    } catch (IOException e) {
                        System.err.printf("ERROR: Unable to read image %s: %s", filePath, e.getMessage());
                        return false;
                    }
                }
            }
            case "MP3", "WAV", "OGG" -> {
                if (!resources.containsKey(filePath)) {
                    try {
                        loadSound(filePath, ResourceManager.class.getResourceAsStream(filePath));
                    } catch (Exception e) {
                        System.err.printf("ERROR: Unable to read sound clip %s: %s", filePath, e.getMessage());
                        return false;
                    }
                }
            }
            case "TTF" -> {
                if (!resources.containsKey(filePath)) {
                    try {
                        loadFont(filePath, ResourceManager.class.getResourceAsStream(filePath));
                    } catch (Exception e) {
                        System.err.printf("ERROR: Unable to read font %s: %s", filePath, e.getMessage());
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private static void loadSound(String path, InputStream stream) {
        SoundClip sc = new SoundClip(path, stream);
        if (sc != null) {
            resources.put(path, sc);
        }
        System.out.printf("INFO: '%s' added as an audio resource", path);
    }

    private static void loadFont(String path, InputStream stream) {
        // load a Font resource
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            if (font != null) {
                resources.put(path, font);
                System.out.printf("INFO: '%s' added as a font resource", path);
            }
        } catch (FontFormatException | IOException e) {
            System.err.printf("ERROR: Unable to read font from %s%n", path);
        }
    }
}
