package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;

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
                if (Optional.ofNullable(e.borderColor).isPresent()) {
                    Stroke b = g.getStroke();
                    g.setColor(e.borderColor);
                    g.setStroke(new BasicStroke(e.borderWidth));
                    g.drawRect(
                            (int) e.position.x, (int) e.position.y,
                            (int) e.size.x, (int) e.size.y);
                    g.setStroke(b);
                }
                g.setColor(e.color);
                g.fillRect(
                        (int) e.position.x, (int) e.position.y,
                        (int) e.size.x, (int) e.size.y);
                break;
            case CIRCLE:
                Ellipse2D el = new Ellipse2D.Double(
                        e.position.x, e.position.y,
                        e.size.x, e.size.y);
                if (Optional.ofNullable(e.borderColor).isPresent()) {
                    Stroke b = g.getStroke();
                    g.setColor(e.borderColor);
                    g.setColor(e.borderColor);
                    g.draw(el);
                    g.setStroke(b);
                }
                g.setColor(e.color);
                g.setPaint(e.color);
                g.fill(el);
                break;
        }
    }
}
