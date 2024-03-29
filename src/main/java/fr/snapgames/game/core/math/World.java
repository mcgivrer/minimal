package fr.snapgames.game.core.math;

import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;

/**
 * The World class to define environment characteristics
 */
public class World {
    Dimension playArea;
    Vector2D gravity;
    Vector2D wind = new Vector2D();
    Material material = Material.AIR;

    public World(Dimension area, Vector2D gravity) {
        this.playArea = area;
        this.gravity = gravity;
    }

    public Vector2D getGravity() {
        return this.gravity;
    }

    public Dimension getPlayArea() {
        return playArea;
    }

    public boolean isNotContaining(GameEntity ge) {
        return ge.position.x < 0
                || ge.position.x + ge.size.x > playArea.width
                || ge.position.y < 0
                || ge.position.y + ge.size.y > playArea.height;
    }

    public World setPlayArea(Dimension pa) {
        this.playArea = pa;
        return this;
    }

    public World setGravity(Vector2D g) {
        this.gravity = g;
        return this;
    }

    public World setMaterial(Material mat) {
        this.material = mat;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public World setWind(Vector2D w) {
        this.wind = w;
        return this;
    }

    public Vector2D getWind() {
        return wind;
    }
}
