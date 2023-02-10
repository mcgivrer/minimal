package fr.snapgames.game.demo101.scenes.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.ParticlesEntity;
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
public class RainEffectBehavior implements Behavior {

    private World world;
    private Color color;
    Vector2D wind = new Vector2D(
            (Math.random() - 2.5) * 5,
            (Math.random() * 25.0));

    public RainEffectBehavior(World w, Color c, Vector2D wind) {
        world = w;
        color = c;
        this.wind = wind;
    }

    public RainEffectBehavior(World w, Color c) {
        world = w;
        color = c;
    }

    @Override
    public void update(Game game, Object entity, double dt) {
        ParticlesEntity pe = (ParticlesEntity) entity;

        pe.getChild().forEach(p -> {
            if (Optional.ofNullable(world.getWind()).isPresent()) {
                p.forces.add(world.getWind());
            }

            p.forces.add(world.getGravity().negate());

            if (p.position.y - p.size.y > pe.size.y ||
                    p.position.x > pe.size.x ||
                    p.position.x < 0.0 ||
                    p.position.y < 0.0) {
                p.setColor(color);
                p.setPosition(new Vector2D(
                        Math.random() * world.getPlayArea().width,
                        Math.random() * world.getPlayArea().height));
            }
        });
    }

    @Override
    public void input(Game game, Object entity) {

    }

    @Override
    public void draw(Game game, Graphics2D g, Object entity) {

    }
}
