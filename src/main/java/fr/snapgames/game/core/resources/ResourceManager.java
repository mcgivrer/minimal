package fr.snapgames.game.core.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link ResourceManager} is a service to load and cache resources like images.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class ResourceManager {

    private static Map<String, Object> resources = new HashMap<>();

    public static BufferedImage loadImage(String filePath) {
        if (!addResource(filePath)) return null;
        return (BufferedImage) resources.get(filePath);
    }

    private static boolean addResource(String filePath) {
        switch (filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase()) {
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
