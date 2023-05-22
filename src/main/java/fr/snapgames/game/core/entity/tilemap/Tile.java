package fr.snapgames.game.core.entity.tilemap;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    private final boolean solid;
    private final Map<String, Object> attributes = new HashMap<>();
    private BufferedImage image;
    private int width;
    private int height;

    public Tile(BufferedImage image, boolean isSolid) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.solid = isSolid;
    }

    public Tile addAttribute(String key, Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public Object getAttribute(String key, Object defaultValue) {
        return this.attributes.getOrDefault(key, defaultValue);
    }

    public boolean isSolid() {
        return this.solid;
    }
}
