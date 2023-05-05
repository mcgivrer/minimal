package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.ParticleBehavior;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.ParticlesEntity;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;

import java.awt.*;
import java.util.Optional;

/**
 * Rain Effect behavior to simulate falling rain
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public class RainEffectBehavior implements ParticleBehavior<ParticlesEntity> {

    private World world;
    private Color color;
    Vector2D wind = new Vector2D(
            (Math.random() - 2.5) * 5,
            (Math.random() * 25.0));

    int nbParticles = 0;
    int counter = 0;

    public RainEffectBehavior(World w, Color c, int nbParticles) {
        this.world = w;
        this.color = c;
        this.wind = world.getWind();
        this.nbParticles = nbParticles;
    }

    public RainEffectBehavior(World w, Color c) {
        world = w;
        color = c;
    }

    @Override
    public void update(Game game, ParticlesEntity pe, double dt) {

        pe.getChild().forEach(p -> {
            if (Optional.ofNullable(world.getWind()).isPresent()) {
                p.forces.add(world.getWind());
            }
            // add gravity force to make rain drops falling :)
            p.forces.add(world.getGravity().negate().multiply(1000.0));

            // TODO this will be replaced by a collision detection event processing.
            double bottomPosition = game.getSceneManager().getActiveScene().getAttribute("rainBottomLevel", pe.size.y * .85);

            if (p.position.y - p.size.y > bottomPosition ||
                    p.position.x > pe.size.x ||
                    p.position.x < 0.0 ||
                    p.position.y < 0.0) {
                p.setColor(color);
                p.setPosition(new Vector2D(
                        Math.random() * world.getPlayArea().width,
                        0));
            }
        });
        // create new particles if needed.
        create(game, pe);
    }

    @Override
    public void create(Game g, ParticlesEntity pes) {
        if (pes.getChild().size() < nbParticles) {
            GameEntity p = new GameEntity(pes.getName() + "_" + (++counter))
                    .setType(EntityType.CIRCLE)
                    .setPhysicType(PhysicType.DYNAMIC)
                    .setSize(new Vector2D(1.0, 1.0))
                    .setPosition(
                            new Vector2D(
                                    world.getPlayArea().getWidth() * Math.random(),
                                    world.getPlayArea().getHeight() * Math.random()))
                    .setColor(Color.CYAN)
                    .setLayer(pes.getLayer())
                    .setPriority(counter)
                    .setMass(0.1)
                    .setMaterial(Material.AIR)
                    .setAttribute("maxSpeedY", 200.0)
                    .setAttribute("maxAccY", 20.0);
            pes.getChild().add(p);
        }
    }
}
