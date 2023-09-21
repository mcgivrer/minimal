package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Camera used to see/follow entity in game viewport.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 */
public class Camera extends GameEntity {
    public GameEntity target;
    public double rotation = 0.0f, tween = 0.0f;

    public double zoom = 1.0;
    public Rectangle2D viewport;

    public Camera(String name) {
        super(name);
        position = new Vector2D(0, 0);
        target = null;
    }

    public Camera setTarget(GameEntity t) {
        this.target = t;
        return this;
    }

    public Camera setViewport(Rectangle2D dim) {
        this.viewport = dim;
        return this;
    }

    public Camera setRotation(double r) {
        this.rotation = r;
        return this;
    }

    public Camera setZoom(double z) {
        this.zoom = z;
        return this;
    }

    public Camera setTween(double tween) {
        this.tween = tween;
        return this;
    }

    public void preDraw(Graphics2D g) {
        AffineTransform af = AffineTransform.getRotateInstance(-rotation,
                viewport.getWidth() * 0.5,
                viewport.getHeight() * 0.5);
        af.translate(-zoom * position.x, -zoom * position.y);
        af.scale(zoom, zoom);
        g.transform(af);
    }

    public void postDraw(Graphics2D g) {
        AffineTransform af = AffineTransform.getRotateInstance(rotation,
                viewport.getWidth() * 0.5,
                viewport.getHeight() * 0.5);
        af.translate(zoom * position.x, zoom * position.y);
        af.scale(1.0 / zoom, 1.0 / zoom);
        g.transform(af);
    }

    public void update(double dt) {
        this.position.x += (target.position.x + (target.size.x * 0.5) - ((viewport.getWidth()) * 0.5) - this.position.x)
                * tween * Math.min(dt, 10);
        this.position.y += (target.position.y + (target.size.y * 0.5) - ((viewport.getHeight()) * 0.5) - this.position.y)
                * tween * Math.min(dt, 10);

        this.viewport.setRect(this.position.x, this.position.y, this.viewport.getWidth(), this.viewport.getHeight());
    }
}
