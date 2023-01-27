package fr.snapgames.game.core.entity;

/**
 * The ParticlesEntity defines a particles system to draw SFX.
 * The child entities are particles and a specific Behavior plugin
 * will be used to animate those child entities.
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public class ParticlesEntity extends GameEntity {
    /**
     * Create a new {@link ParticlesEntity} with a name.
     *
     * @param name Name of the new entity.
     */
    public ParticlesEntity(String name) {
        super(name);
    }


}
