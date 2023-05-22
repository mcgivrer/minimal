package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;

/**
 * RendererPlugin to draw specific Entity on screen.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public interface RendererPlugin<T extends GameEntity> {
    Class<?> getObjectClass();

    void draw(Renderer r, Graphics2D g, T e);

    default void drawDebug(Renderer r, Graphics2D g, T e, double scale) {
        g.setFont(r.getDebugFont());
        g.setColor(Color.ORANGE);
        switch (e.getType()) {
            case RECTANGLE -> {
                g.drawRect(
                        (int) (e.collisionBox.getBounds2D().getX() * scale),
                        (int) (e.collisionBox.getBounds2D().getY() * scale),
                        (int) (e.collisionBox.getBounds2D().getWidth() * scale),
                        (int) (e.collisionBox.getBounds2D().getHeight() * scale));

            }
            case CIRCLE -> {
                g.drawOval(
                        (int) (e.collisionBox.getBounds2D().getX() * scale),
                        (int) (e.collisionBox.getBounds2D().getY() * scale),
                        (int) (e.collisionBox.getBounds2D().getWidth() * scale),
                        (int) (e.collisionBox.getBounds2D().getHeight() * scale));
            }
        }
        int il = 0;
        for (String s : e.getDebugInfo()) {
            g.drawString(s, (int) ((e.position.x + e.size.x + 4.0) * scale), (int) ((e.position.y + il) * scale));
            il += 10;
        }

    }
}
