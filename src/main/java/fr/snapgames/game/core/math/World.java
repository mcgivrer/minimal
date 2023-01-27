package fr.snapgames.game.core.math;

import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;

/**
 * The World class to define environment characteristics
 */
public class World {
    private Dimension playArea;
    private Vector2D gravity;

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
}
