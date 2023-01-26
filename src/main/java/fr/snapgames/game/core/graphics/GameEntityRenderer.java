package fr.snapgames.game.core.graphics;

import fr.snapgames.game.core.entity.GameEntity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Optional;

/**
 * TextEntityRenderer plugin to draw TextEntity on screen.
 * 
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class GameEntityRenderer implements RendererPlugin<GameEntity> {
    @Override
    public Class<?> getObjectClass() {
        return GameEntity.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, GameEntity e) {
        switch (e.type) {
            case IMAGE:
                if (Optional.ofNullable(e.image).isPresent()) {
                    boolean direction = e.speed.x > 0;
                    if (direction) {
                        g.drawImage(e.image,
                                (int) e.position.x, (int) e.position.y,
                                null);
                    } else {
                        g.drawImage(e.image,
                                (int) (e.position.x + e.size.x), (int) e.position.y,
                                (int) -e.size.x, (int) e.size.y,
                                null);
                    }
                }
                break;
            case RECTANGLE:
                g.setColor(e.color);
                g.fillRect(
                        (int) e.position.x, (int) e.position.y,
                        (int) e.size.x, (int) e.size.y);
                break;
            case CIRCLE:
                g.setColor(e.color);
                g.setPaint(e.color);
                g.fill(new Ellipse2D.Double(
                        e.position.x, e.position.y,
                        e.size.x, e.size.y));
                break;
        }
    }
}
