package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.entity.tilemap.Tile;
import fr.snapgames.game.core.entity.tilemap.TileLayer;
import fr.snapgames.game.core.entity.tilemap.TileSet;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileMap extends GameEntity {

    private List<TileLayer> layers = new ArrayList<>();
    private TileSet tileSet;

    /**
     * Create a new {@link TileMap} with a name, and set all characteristics to
     * default values.
     *
     * @param name Name of the new {@link TileMap}.
     */
    public TileMap(String name) {
        super(name);
    }

    public TileMap() {
        super("tm_noname");
    }


    public TileMap setTileSet(TileSet ts) {
        this.tileSet = ts;
        return this;
    }

    public boolean isSolidAt(int x, int y) {
        AtomicBoolean tileIsSolid = new AtomicBoolean(false);
        layers.stream().filter(l -> l.isTiled()).forEach(l -> {
            Tile t = tileSet.getTile(l.get(x, y));
            if (t.isSolid()) {
                tileIsSolid.set(true);
            }

        });
        return tileIsSolid.get();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TileLayer> getTileLayers() {
        return layers;
    }
}
