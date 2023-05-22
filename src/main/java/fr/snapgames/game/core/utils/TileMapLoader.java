package fr.snapgames.game.core.utils;

import fr.snapgames.game.core.entity.TileMap;
import fr.snapgames.game.core.entity.tilemap.TileLayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class TileMapLoader {

    public static TileMap load(String file) {
        TileMap tilemap = new TileMap();
        try {
            Properties mapdata = new Properties();
            mapdata.load(TileMapLoader.class.getResourceAsStream("/maps/map-01.properties"));
            mapdata.stringPropertyNames().stream().forEach(prop -> {
                extractTileMapName(tilemap, mapdata, prop);
                extractLayers(tilemap, mapdata, prop);
            });
        } catch (IOException e) {
            System.err.printf("ERROR|TileMapLoader|Unable to load tilemap from file %s%n", file);
        }

        return tilemap;
    }

    private static void extractTileMapName(TileMap tilemap, Properties mapdata, String prop) {
        if (prop.startsWith("map.name")) {
            tilemap.setName(mapdata.getProperty("map.name"));
        }
    }

    private static void extractLayers(TileMap tilemap, Properties mapdata, String prop) {
        if (prop.startsWith("map.layer.")) {
            TileLayer tl = null;
            int id = Integer.valueOf(prop.substring(("map.layer.").length(), ("map.layer.").length()+1));
            tl = getOrCreateTileLayer(tilemap, id);
            extractTileLayerSize(mapdata, prop, tl);
        }
    }

    private static TileLayer getOrCreateTileLayer(TileMap tilemap, int id) {
        TileLayer tl;
        if (tilemap.getTileLayers().size() < id + 1) {
            tl = new TileLayer("layer_" + id);
            tilemap.getTileLayers().add(tl);
        } else {
            tl = tilemap.getTileLayers().get(id);
        }
        return tl;
    }

    private static void extractTileLayerSize(Properties mapdata, String prop, TileLayer tl) {
        if (prop.endsWith(".size")) {
            String[] vals = ((String) mapdata.get(prop)).split("x");
            tl.setSize(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]));
        }
    }
}
