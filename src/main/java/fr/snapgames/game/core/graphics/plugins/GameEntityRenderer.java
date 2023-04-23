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
            case IMAGE -> {
                if (Optional.ofNullable(e.image).isPresent()) {
                    boolean direction = e.direction > 0;
                    if (direction) {
                        g.drawImage(e.image,
                                (int) e.position.x, (int) e.position.y,
                                (int) e.size.x, (int) e.size.y,
                                null);
                    } else {
                        g.drawImage(e.image,
                                (int) (e.position.x + e.size.x), (int) e.position.y,
                                (int) -e.size.x, (int) e.size.y,
                                null);
                    }
                }
            }
            case RECTANGLE -> {
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
            }
            case CIRCLE -> {
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
            }
        }
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, GameEntity v) {
        g.setColor(Color.ORANGE);
        g.drawRect((int) v.position.x, (int) v.position.y,
                (int) v.size.x, (int) v.size.y);
        int il = 0;
        for (String s : v.getDebugInfo()) {
            g.drawString(s, (int) (v.position.x + v.size.x + 4.0), (int) v.position.y + il);
            il += 10;
        }

    }
}
