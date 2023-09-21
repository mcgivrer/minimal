package fr.snapgames.game.core.math;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.behaviors.CollisionResponseBehavior;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.Influencer;

import java.awt.*;
import java.util.*;
import java.util.List;
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
    private World world;

    private final Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    private final Map<String, GameEntity> colliders = new ConcurrentHashMap<>();


    private final double maxAcceleration;
    private final double maxVelocity;
    private final List<CollisionEvent> collisions = new ArrayList<CollisionEvent>();

    public PhysicEngine(Game g) {
        this.game = g;
        Configuration config = g.getConfiguration();
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
        entities.forEach(this::addEntity);
    }

    public void addEntity(GameEntity e) {
        this.entities.put(e.getName(), e);
        if (e.isCollider()) {
            this.colliders.put(e.getName(), e);
        }
    }

    public void update(double elapsed) {
        double time = elapsed * TIME_FACTOR;
        collisions.clear();
        entities.values().stream()
                .filter(e -> !(e instanceof Influencer))
                .forEach(entity -> {

                    entity.setContact(0);
                    if (entity.isCollider()) {
                        detectCollision(entity);
                    }
                    updateEntity(entity, time);
                    if (Optional.ofNullable(world).isPresent()) {
                        constrainEntityToWorld(world, entity);
                    }
                    if (entity.isCollider()) {
                        detectCollision(entity);
                    }
                });
        // process Influencer behavior's update
        entities.values().stream()
                .filter(e -> e instanceof Influencer)
                .forEach(entity -> {
                    for (Behavior b : entity.behaviors) {
                        b.update(game, entity, elapsed);
                    }
                });
    }

    private void detectCollision(GameEntity entity) {
        colliders.values().stream()
                .filter(e ->
                        !(e instanceof Influencer)
                                && entity.getId() != e.getId()
                                && e.isActive()
                                && e.getPhysicType().equals(PhysicType.DYNAMIC)
                                && entity.getCollisionBox().intersects(e.getCollisionBox().getBounds2D()))
                .forEach(ec -> {

                    // parse all entities to detect possible collision and call the filtered CollisionResponseBehavior.
                    entity.getBehaviors().stream()
                            .filter(b -> b instanceof CollisionResponseBehavior
                                    && !((CollisionResponseBehavior<?>) b).getFilteredNames().equals(""))
                            .forEach(crb -> {
                                String[] etl = ((CollisionResponseBehavior<?>) crb).getFilteredNames().split(",");
                                if (Arrays.stream(etl).anyMatch(fn -> fn.contains(ec.getName()))) {
                                    CollisionEvent collision = new CollisionEvent(entity, ec);
                                    collisions.add(collision);
                                    ((CollisionResponseBehavior<?>) crb)
                                            .collide(game, collision);
                                }
                            });
                });
    }

    /**
     * Compute new acceleration speed and position for the {@link GameEntity} entity, according to the elapsed time.
     *
     * <p>The simplified famous <a href="https://www1.grc.nasa.gov/beginners-guide-to-aeronautics/newtons-laws-of-motion/"
     * title="go and visit NASA's web site about Newton's Laws of motion">Newton's physic's laws of motion</a>
     * are applied to the {@link GameEntity}.</p>
     *
     * <p><strong>NOTE</strong> The acceleration and velocity for the {@link GameEntity} are threshold
     * by a maximum speed define by the configuration attribute {@link ConfigAttribute#PHYSIC_MAX_ACCELERATION_X}
     * and {@link ConfigAttribute#PHYSIC_MAX_SPEED_X}, BUT those value can be superseded by :
     * <ul>
     *     <li>the {@link GameEntity#attributes} entry <code>maxAccelX</code>,<code>maxAccelY</code> for x and y acceleration,</li>
     *     <li>the {@link GameEntity#attributes} entry <code>maxVelX</code> and <code>maxVelY</code> for x and y velocity.</li>
     * </ul>
     * </p>
     *
     * @param entity  the {@link GameEntity} to be processed by the {@link PhysicEngine}.
     * @param elapsed the elapsed time (in millisecond) since previous call.
     */
    public void updateEntity(GameEntity entity, double elapsed) {

        if (!entity.isStickToCamera() && entity.physicType.equals(PhysicType.DYNAMIC)) {
            entity.rotation = new Vector2D(-1.0, 0.0).angle(world.getGravity());
            // apply gravity
            entity.forces.add(world.getGravity().negate());
            // Apply influencer Effects (Material and force impacted)
            Material material = appliedInfluencerToEntity(entity, world);
            // compute acceleration
            entity.acceleration = entity.acceleration.addAll(entity.forces);
            entity.acceleration = entity.acceleration.multiply(entity.mass * material.density);
            entity.acceleration.maximize(
                            (double) entity.getAttribute("maxAccelX", maxAcceleration),
                            (double) entity.getAttribute("maxAccelY", maxAcceleration))
                    .thresholdToZero(0.01);

            // compute velocity
            double roughness = 1.0;
            if (entity.contact > 0) {
                roughness = material.roughness;
            } else {
                roughness = world.getMaterial().roughness;
            }
            entity.speed = entity.speed.add(entity.acceleration.multiply(elapsed * elapsed * 0.5)).multiply(roughness);
            entity.speed.maximize(
                            (double) entity.getAttribute("maxVelX", maxVelocity),
                            (double) entity.getAttribute("maxVelY", maxVelocity))
                    .thresholdToZero(0.8);

            // compute position
            entity.position = entity.position.add(entity.speed.multiply(elapsed));
            entity.getChild().forEach(c -> updateEntity(c, elapsed));
            entity.forces.clear();
            entity.updateBox();
        }
        for (Behavior b : entity.behaviors) {
            b.update(game, entity, elapsed);
        }
        if (!entity.currentAnimation.equals("") && entity.animations.containsKey(entity.currentAnimation)) {
            entity.animations.get(entity.currentAnimation).update((int) elapsed);
        }
    }

    private Material appliedInfluencerToEntity(GameEntity e, World world) {
        Material material = e.material.copy();
        List<GameEntity> influencerList = entities.values().stream()
                .filter(i -> i instanceof Influencer)
                .filter(i -> i.box.intersects(e.box.getBounds2D()))
                .toList();
        for (GameEntity ge : influencerList) {
            Influencer f = (Influencer) ge;
            material = material.merge(f.material);
            double buoyant = f.material.density * world.gravity.y *
                    (e.mass / e.material.density) * 0.005;
            e.addForce(new Vector2D(0.0, buoyant));
            e.addForces(f.forces);
        }
        return material;
    }

    /**
     * Constrain the GameEntity ge to stay in the world play area.
     *
     * @param world the defined World for the Game
     * @param ge    the GameEntity to be checked against world's play area constrains
     * @see GameEntity
     * @see World
     */
    private void constrainEntityToWorld(World world, GameEntity ge) {
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
                ge.speed.x = ge.speed.x * -ge.material.elasticity;
                ge.contact |= 1;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
                ge.speed.x = ge.speed.x * -ge.material.elasticity;
                ge.contact |= 2;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
                ge.speed.y = ge.speed.y * -ge.material.elasticity;
                ge.contact |= 4;
            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
                ge.speed.y = ge.speed.y * -ge.material.elasticity;
                ge.contact |= 8;
            }
        }
    }

    public void reset() {
        entities.clear();
        colliders.clear();
    }

    public List<CollisionEvent> getCollisionEvents() {
        return collisions;
    }

    public void removeEntity(String entityName) {
        entities.remove(entityName);
    }
}
