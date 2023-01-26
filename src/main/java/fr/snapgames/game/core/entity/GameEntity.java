package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Entity manipulated by Game.
 *
 * @author Frédéric Delorme
 */
public class GameEntity {
    public String name = "noname";
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
    public BufferedImage image;
    public boolean active;
    public long life = -1;
    public long duration;

    public Material material;
    public PhysicType physicType = PhysicType.DYNAMIC;

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
        attributes.put("maxSpeed", 8.0);
        attributes.put("maxAcceleration", 3.0);
        attributes.put("mass", 10.0);
    }

    public void update(Game g, double dt) {
        for (Behavior b : behaviors) {
            b.update(g, this, dt);
        }
        if (!isStickToCamera()) {
            this.acceleration = this.acceleration.addAll(this.forces);
            this.acceleration = this.acceleration.multiply((double) attributes.get("mass"));

            this.acceleration.maximize((double) attributes.get("maxAcceleration"));

            this.speed = this.speed.add(this.acceleration.multiply(dt)).multiply(material.roughness);
            this.speed.maximize((double) attributes.get("maxSpeed"));

            this.position = this.position.add(this.speed.multiply(dt));
            this.forces.clear();
        }
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
        this.image = i;
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
}
