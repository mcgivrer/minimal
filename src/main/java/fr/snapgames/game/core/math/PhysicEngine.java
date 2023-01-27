package fr.snapgames.game.core.math;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;
import java.util.Collection;
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

    private final Game game;
    private Configuration config;
    private World world;

    private Map<String, GameEntity> entities = new ConcurrentHashMap<>();

    private final double maxAcceleration;
    private final double maxVelocity;

    public PhysicEngine(Game g) {
        this.game = g;
        config = g.getConfiguration();
        maxAcceleration = config.getDouble("game.physic.limit.acceleration.max", 4.0);
        maxVelocity = config.getDouble("game.physic.limit.velocity.max", 4.0);
        Dimension playArea = config.getDimension("game.physic.world.playarea", new Dimension(320, 200));
        Vector2D gravity = config.getVector2D("game.physic.world.gravity", new Vector2D(0, 0));
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
        entities.values().stream()
                .filter(e -> e.isActive()
                        && e.physicType.equals(PhysicType.DYNAMIC))
                .forEach(entity -> {
                    updateEntity(entity, elapsed);
                    if (Optional.ofNullable(world).isPresent()) {
                        constrainEntityToWorld(world, entity);
                    }
                });
    }

    public void updateEntity(GameEntity entity, double elapsed) {
        for (Behavior b : entity.behaviors) {
            b.update(game, entity, elapsed);
        }
        if (!entity.isStickToCamera()) {
            // apply gravity
            entity.forces.add(world.getGravity().negate());

            // compute acceleration
            entity.acceleration = entity.acceleration.addAll(entity.forces).multiply(entity.material.density);
            entity.acceleration = entity.acceleration.multiply((double) entity.mass);

            entity.acceleration.maximize((double) entity.getAttribute("maxAcceleration", maxAcceleration));

            // compute velocity

            entity.speed = entity.speed.add(entity.acceleration.multiply(elapsed));
            if (entity.contact > 0) {
                entity.speed = entity.speed.multiply(entity.material.roughness);
            }
            entity.speed.maximize((double) entity.getAttribute("maxVelocity", maxVelocity));

            // compute position
            entity.position = entity.position.add(entity.speed.multiply(elapsed));
            entity.forces.clear();
        }
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
                ge.contact += 1;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
                ge.contact += 2;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
                ge.contact += 4;

            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
                ge.contact += 8;

            }
            ge.speed = ge.speed.multiply(-ge.material.elasticity);
        }
    }
}
