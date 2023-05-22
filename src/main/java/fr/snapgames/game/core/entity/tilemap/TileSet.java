package fr.snapgames.game.core.entity.tilemap;

import java.awt.image.BufferedImage;
import java.util.*;

public class TileSet {
    private BufferedImage image;
    private Map<String, Tile> tiles = new HashMap<>();
    private int tileWidth;
    private int tileHeight;

    public TileSet() {
    }

    public TileSet setImageSource(BufferedImage img) {
        this.image = img;
        return this;
    }

    public TileSet setTileSize(int tw, int th) {
        this.tileWidth = tw;
        this.tileHeight = th;
        return this;
    }


    public TileSet createTile(String key, int px, int py, int pw, int ph, boolean isSolid) {
        if (Optional.ofNullable(image).isPresent()) {
            Tile tile = new Tile(image.getSubimage(px * pw, py * ph, pw, ph), isSolid);
            tiles.put(key, tile);
        }
        return this;
    }

    public TileSet addAttributeToTile(String tileKey, String attrName, Object attrValue) {
        tiles.get(tileKey).addAttribute(attrName, attrValue);
        return this;
    }

    public Tile getTile(char c) {
        return tiles.get(c);
    }

}
