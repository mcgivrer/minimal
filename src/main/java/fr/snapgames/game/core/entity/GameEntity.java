package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.graphics.plugins.RendererPlugin;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Entity manipulated by Game.
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 */
public class GameEntity {
    public static long index = 0;
    public String name = "noname" + (index++);
    public Vector2D position = new Vector2D(0, 0);
    public Vector2D speed = new Vector2D(0, 0);
    public Vector2D acceleration = new Vector2D(0, 0);
    public Vector2D size = new Vector2D(16, 16);
    public EntityType type = EntityType.RECTANGLE;
    public double rotation = 0.0;

    public boolean stickToCamera = false;
    public List<Vector2D> forces = new ArrayList<>();

    public Color color = Color.RED;
    public Color shadowColor;
    public int shadowWidth;
    public Color borderColor;
    public int borderWidth;

    public Map<String, Object> attributes = new HashMap<>();
    public List<Behavior> behaviors = new ArrayList<>();
    public Rectangle2D box;

    private List<GameEntity> child = new ArrayList<>();

    public BufferedImage image;

    public boolean active;
    public long life;
    public long duration;

    public Material material;
    public PhysicType physicType = PhysicType.DYNAMIC;
    public double mass;

    public int direction;
    public int contact;

    private Class<?> renderedByPlugin;

    private int layer;
    private int priority;

    /**
     * Create a new GameEntity with a name.
     *
     * @param name Name of the new entity.
     */
    public GameEntity(String name) {
        this.name = name;
        this.active = true;
        this.physicType = PhysicType.DYNAMIC;
        this.material = Material.DEFAULT;
        this.direction = 1;
        this.life = -1;
        this.duration = -1;
        this.layer = 1;
        this.priority = 1;
        this.box = new Rectangle2D.Double();
        attributes.put("maxSpeed", 8.0);
        attributes.put("maxAcceleration", 3.0);

    }

    public GameEntity setPosition(Vector2D pos) {
        this.position = pos;
        return this;
    }

    public GameEntity stickToCamera(boolean flag) {
        this.stickToCamera = flag;
        return this;
    }

    public boolean isStickToCamera() {
        return stickToCamera;
    }

    public GameEntity setSize(Vector2D s) {
        this.size = s;
        return this;
    }

    public GameEntity setType(EntityType t) {
        this.type = t;
        return this;
    }

    public GameEntity setImage(BufferedImage i) {
        if (Optional.ofNullable(i).isPresent()) {
            this.image = i;
            setType(EntityType.IMAGE);
            setSize(new Vector2D(i.getWidth(), i.getHeight()));
        }
        return this;
    }


    public GameEntity setSpeed(Vector2D speed) {
        this.speed = speed;
        return this;
    }

    public GameEntity setMaterial(Material m) {
        this.material = m;
        return this;
    }

    public GameEntity setMass(double m) {
        this.mass = m;
        return this;
    }

    public Collection<String> getDebugInfo() {
        List<String> ls = new ArrayList<>();
        ls.add(String.format("name:%s", name));
        ls.add(String.format("pos: %04.2f,%04.2f", this.position.x, this.position.y));
        ls.add(String.format("spd: %04.2f,%04.2f", this.speed.x, this.speed.y));
        ls.add(String.format("acc: %04.2f,%04.2f", this.acceleration.x, this.acceleration.y));
        ls.add(String.format("mat: %s[%04.2f,%04.2f,%04.2f]",
                this.material.name,
                this.material.density,
                this.material.elasticity,
                this.material.roughness));

        return ls;
    }

    public GameEntity setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public GameEntity addChild(GameEntity ge) {
        child.add(ge);
        return this;
    }

    public List<GameEntity> getChild() {
        return child;
    }

    public GameEntity setColor(Color color) {
        this.color = color;
        return this;
    }

    public GameEntity setShadowColor(Color c) {
        this.shadowColor = c;
        return this;
    }

    public GameEntity setShadowWidth(int sw) {
        this.shadowWidth = sw;
        return this;
    }

    public GameEntity setBorderColor(Color c) {
        this.borderColor = c;
        return this;
    }

    public GameEntity setBorderWidth(int sw) {
        this.borderWidth = sw;
        return this;
    }

    public GameEntity addBehavior(Behavior b) {
        this.behaviors.add(b);
        return this;
    }

    public Object getAttribute(String attrName, Object defaultValue) {
        return attributes.getOrDefault(attrName, defaultValue);
    }

    public boolean isActive() {
        return this.active;
    }

    public GameEntity setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Class<?> getRenderedBy() {
        return renderedByPlugin;
    }

    public GameEntity setDrawnBy(Class<?> rendererPluginClass) {
        this.renderedByPlugin = rendererPluginClass;
        return this;
    }

    public GameEntity setDirection(int d) {
        this.direction = d;
        return this;
    }

    public GameEntity setLayer(int l) {
        this.layer = l;
        return this;
    }

    public GameEntity setPriority(int p) {
        this.priority = p;
        return this;
    }

    public int getLayer() {
        return layer;
    }

    public int getPriority() {
        return priority;
    }

    public GameEntity setPhysicType(PhysicType pt) {
        this.physicType = pt;
        return this;
    }

    public void updateBox() {
        this.box = new Rectangle2D.Double(position.x, position.y, size.x, size.y);
    }
}
