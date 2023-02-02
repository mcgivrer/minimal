package fr.snapgames.game.demo101.scenes.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.ParticlesEntity;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;

import java.awt.*;

/**
 * Rain Effect behavior to simulate falling rain
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public class RainEffectBehavior implements Behavior {

    private World world;
    private Color color;

    public RainEffectBehavior(World w, Color c) {
        world = w;
        color = c;
    }

    @Override
    public void update(Game game, Object entity, double dt) {
        ParticlesEntity pe = (ParticlesEntity) entity;
        pe.getChild().forEach(p -> {
            p.forces.add(
                    new Vector2D(
                            (Math.random() - 0.5) * 0.1,
                            (Math.random() * 25.0)));
            p.forces.add(new Vector2D(
                    Math.random() * 10.0, 0.0));
            p.forces.add(world.getGravity());

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
