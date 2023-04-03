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

    default void drawDebug(Renderer r, Graphics2D g, T e) {
        g.setColor(Color.ORANGE);
        g.draw(e.box);
    }
}
