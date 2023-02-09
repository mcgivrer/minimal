package fr.snapgames.game.core.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ResourceManager} is a static library providing utility to load and cache resources like images.
 * <p>
 * Usage:
 * <p>
 * A very simple approach consists in caching the read object, ad pull them from cache when already existing.
 * <ol>
 *     <li>Load Image resource : {@link ResourceManager#loadImage(String)} :
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
     * Request for an image. If image already exists in cache, pull it fom.
     *
     * @param filePath the path to the image to be loaded.
     * @return a BufferedImage instance corresponding to the loaded image. If file not found, return null.
     */
    public static BufferedImage loadImage(String filePath) {
        if (!addResource(filePath)) return null;
        return (BufferedImage) resources.get(filePath);
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
                        System.err.printf("Game:Unable to read image %s: %s", filePath, e.getMessage());
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
