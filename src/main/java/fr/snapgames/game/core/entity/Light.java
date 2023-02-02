package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Light extends GameEntity {

    private float intensity;

    private LightType type;
    private GameEntity target;
    private LightType lightType;
    // parameter to generate intensity fluctuation
    private float dIntensity = 1.0f;
    private double distance;
    public float[] dist = {0f, 1f};
    public Color[] colors = {new Color(0.0f, 0.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 0.0f, 1.0f)};

    /**
     * Create a new Light with a name.
     *
     * @param name Name of the new entity.
     */
    public Light(String name) {
        super(name);
        this.setPhysicType(PhysicType.STATIC);
    }

    /**
     * Create a {@link LightType#AMBIANT} light with a name, and corresponding to the playArea, and with the required intensity.
     *
     * @param name      name of that ambiant light
     * @param playArea  the area covered by this ambiant light
     * @param intensity the intensity for that light (from 0.0f to 1.0f)
     */
    public Light(String name, Rectangle2D playArea, float intensity) {
        this(name);
        setPosition(new Vector2D());
        setSize(new Vector2D(playArea.getWidth(), playArea.getHeight()));
        this.lightType = LightType.AMBIANT;
        this.intensity = intensity;
    }

    /**
     * Create a {@link LightType#SPOT} light centered on target with a size and an intensity.
     *
     * @param name      the name for that spotlight
     * @param radius    the radius of the round light area
     * @param intensity the intensity for that light (0.0f to 1.0f)
     * @param target    the target tracked by that light (centered on).
     */
    public Light(final String name,
                 final double radius,
                 final float intensity,
                 final GameEntity target) {
        this(name);
        setPosition(target.position);
        setSize(new Vector2D(radius, radius));
        this.intensity = intensity;
        this.lightType = LightType.SPOT;
        this.target = target;
    }

    /**
     * Create a {@link LightType#SPOT} light at x,y with a radius and an intensity.
     *
     * @param name      the name for that spotlight
     * @param x         the horizontal position for that spotlight
     * @param y         the vertical position for that spotlight
     * @param radius    the radius of the round light area
     * @param intensity the intensity for that light (0.0f to 1.0f)
     */
    public Light(final String name,
                 final double x, final double y,
                 final double radius,
                 final float intensity) {
        this(name);
        setPosition(new Vector2D(x, y));
        setSize(new Vector2D(radius, radius));
        this.intensity = intensity;
        this.lightType = LightType.SPOT;
    }

    public LightType getLightType() {
        return lightType;
    }

    public GameEntity getTarget() {
        return target;
    }

    public float getIntensity() {
        return intensity;
    }

    public Light setIntensity(float i) {
        this.intensity = i;
        return this;
    }

    public float getDeltaIntensity() {
        return dIntensity;
    }

    public Light setDeltaIntensity(float di) {
        this.dIntensity = di;
        return this;
    }
}
