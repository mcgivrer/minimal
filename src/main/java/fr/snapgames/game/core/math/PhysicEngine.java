package fr.snapgames.game.core.math;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : M313104
 * @mailto : buy@mail.com
 * @created : 26/01/2023
 **/
public class PhysicEngine {
    private final Game game;
    private Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    private World world;

    public PhysicEngine(Game g) {
        this.game = g;
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
            entity.acceleration = entity.acceleration.multiply((double) entity.getAttribute("mass", 1.0));

            entity.acceleration.maximize((double) entity.getAttribute("maxAcceleration", 1.0));

            // compute velocity
            entity.speed = entity.speed.add(entity.acceleration.multiply(elapsed)).multiply(entity.material.roughness);
            entity.speed.maximize((double) entity.getAttribute("maxSpeed", 1.0));

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
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
            }
            ge.speed = ge.speed.multiply(-ge.material.elasticity);
        }
    }
}
