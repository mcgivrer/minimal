package fr.snapgames.game.core.entity.tilemap;

import fr.snapgames.game.core.entity.GameEntity;

import java.awt.image.BufferedImage;

public class TileLayer extends GameEntity {
    private BufferedImage backgroundImage;

    private char[][] map;

    private int width;
    private int height;

    /**
     * Create a new {@link TileLayer} with a name, and set all characteristics to
     * default values.
     *
     * @param name Name of the new entity.
     */
    public TileLayer(String name) {
        super(name);
    }

    public TileLayer setGridSize(int width, int height) {
        this.width = width;
        this.height = height;
        map = new char[width][height];
        return this;
    }

    public TileLayer setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public void set(int x, int y, char value) {
        this.map[x][y] = value;
    }

    public char get(int x, int y) {
        return this.map[x][y];
    }

    public boolean isBackgroundImage() {
        return this.backgroundImage != null;
    }

    public boolean isTiled() {
        return this.backgroundImage == null;
    }

}
