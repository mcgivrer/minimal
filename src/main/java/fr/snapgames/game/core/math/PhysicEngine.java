package fr.snapgames.game.core.math;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.Influencer;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link PhysicEngine} compute acceleration, velocity and position for all {@link GameEntity}
 * into the {@link World#playArea}, according to the {@link World} object
 *
 * @author : Frédéric Delorme
 * @since 0.0.2
 **/
public class PhysicEngine {

    private static final double TIME_FACTOR = 0.45;
    private final Game game;
    private Configuration config;
    private World world;

    private Map<String, GameEntity> entities = new ConcurrentHashMap<>();

    private final double maxAcceleration;
    private final double maxVelocity;

    public PhysicEngine(Game g) {
        this.game = g;
        config = g.getConfiguration();
        maxAcceleration = (double) config.get(ConfigAttribute.PHYSIC_MAX_ACCELERATION_X);
        maxVelocity = (double) config.get(ConfigAttribute.PHYSIC_MAX_SPEED_X);
        Dimension playArea = (Dimension) config.get(ConfigAttribute.PLAY_AREA_SIZE);
        Vector2D gravity = (Vector2D) config.get(ConfigAttribute.PHYSIC_GRAVITY);
        world = new World(playArea, gravity);
    }

    public PhysicEngine setWorld(World w) {
        this.world = w;
        return this;
    }

    public World getWorld() {
        return this.world;
    }

    public void addEntities(Collection<GameEntity> entities) {
        entities.stream().forEach(e -> this.entities.put(e.name, e));
    }

    public void addEntity(GameEntity e) {
        this.entities.put(e.name, e);
    }

    public void update(double elapsed) {
        double time = elapsed * TIME_FACTOR;
        entities.values().stream()
                .filter(e -> e.isActive() && !(e instanceof Influencer))
                .forEach(entity -> {
                    updateEntity(entity, time);
                    if (Optional.ofNullable(world).isPresent()) {
                        constrainEntityToWorld(world, entity);
                    }
                });
    }

    public void updateEntity(GameEntity entity, double elapsed) {

        if (!entity.isStickToCamera() && entity.physicType.equals(PhysicType.DYNAMIC)) {
            // apply gravity
            entity.forces.add(world.getGravity().negate());
            // Apply influencer Effects (Material and force impacted)
            Material material = appliedInfluencerToEntity(entity, world);
            // compute acceleration
            entity.acceleration = entity.acceleration.addAll(entity.forces).multiply(material.density);
            entity.acceleration = entity.acceleration.multiply((double) entity.mass);
            entity.acceleration.maximize((double) entity.getAttribute("maxAcceleration", maxAcceleration));

            // compute velocity
            double roughness = entity.contact == 0 ? world.getMaterial().roughness : world.getMaterial().roughness * material.roughness;
            entity.speed = entity.speed.add(entity.acceleration.multiply(elapsed)).multiply(roughness);
            entity.speed.maximize((double) entity.getAttribute("maxVelocity", maxVelocity));

            // compute position
            entity.position = entity.position.add(entity.speed.multiply(elapsed));
            entity.getChild().forEach(c -> updateEntity(c, elapsed));
            entity.forces.clear();
            entity.updateBox();
        }
        for (Behavior b : entity.behaviors) {
            b.update(game, entity, elapsed);
        }
    }

    private Material appliedInfluencerToEntity(GameEntity e, World world) {
        Material material = e.material.copy();
        List<GameEntity> influencerList = entities.values().stream()
                .filter(i -> i instanceof Influencer)
                .filter(i -> i.box.intersects(e.box.getBounds2D()))
                .toList();
        for (GameEntity ge : influencerList) {
            material = material.merge(ge.material);
            e.addForces(ge.forces);
        }
        return material;
    }

    /**
     * Constrain the GameEntity ge to stay in the world play area.
     *
     * @param world the defne World for the Game
     * @param ge    the GameEntity to be checked against world's play area constrains
     * @see GameEntity
     * @see World
     */
    private void constrainEntityToWorld(World world, GameEntity ge) {
        ge.contact = 0;
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
                ge.speed.x = ge.speed.x * -ge.material.elasticity;
                ge.contact += 1;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
                ge.speed.x = ge.speed.x * -ge.material.elasticity;
                ge.contact += 2;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
                ge.speed.y = ge.speed.y * -ge.material.elasticity;
                ge.contact += 4;

            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
                ge.speed.y = ge.speed.y * -ge.material.elasticity;
                ge.contact += 8;

            }
        }
    }

    public void reset() {
        entities.clear();
    }
}
