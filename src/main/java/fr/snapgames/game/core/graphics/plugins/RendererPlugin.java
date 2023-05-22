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
        g.drawRect(
                (int) (e.box.getBounds2D().getX() * scale),
                (int) (e.box.getBounds2D().getY()),
                (int) (e.box.getBounds2D().getWidth()),
                (int) (e.box.getBounds2D().getHeight()));
        g.setColor(Color.ORANGE);
        int il = 0;
        for (String s : e.getDebugInfo()) {
            g.drawString(s, (int) ((e.position.x + e.size.x + 4.0) * scale), (int) ((e.position.y + il) * scale));
            il += 10;
        }

    }
}
