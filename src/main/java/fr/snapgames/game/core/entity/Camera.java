package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;

/**
 * Camera used to see/follow entity in game viewport.
 *
 * @author Frédéric Delorme
 */
public class Camera {
    public String name;
    public Vector2D position;
    public GameEntity target;
    public double rotation = 0.0f, tween = 0.0f;
    public Dimension viewport;

    public Camera(String name) {
        this.name = name;
        position = new Vector2D(0, 0);
        target = null;
    }

    public Camera setTarget(GameEntity t) {
        this.target = t;
        return this;
    }

    public Camera setViewport(Dimension dim) {
        this.viewport = dim;
        return this;
    }

    public Camera setRotation(double r) {
        this.rotation = r;
        return this;
    }

    public Camera setTween(double tween) {
        this.tween = tween;
        return this;
    }

    public void preDraw(Graphics2D g) {
        g.translate(-position.x, -position.y);
        g.rotate(-rotation);
    }

    public void postDraw(Graphics2D g) {

        g.rotate(rotation);
        g.translate(position.x, position.y);
    }

    public void update(double dt) {

        this.position.x += Math
                .ceil((target.position.x + (target.size.x * 0.5) - ((viewport.width) * 0.5) - this.position.x)
                        * tween * Math.min(dt, 10));
        this.position.y += Math
                .ceil((target.position.y + (target.size.y * 0.5) - ((viewport.height) * 0.5) - this.position.y)
                        * tween * Math.min(dt, 10));
    }
}
