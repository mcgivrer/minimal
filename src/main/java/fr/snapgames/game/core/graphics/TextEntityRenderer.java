package fr.snapgames.game.core.graphics;

import fr.snapgames.game.core.entity.TextEntity;

import java.awt.*;

/**
 * TextEntityRenderer plugin to draw TextEntity on screen.
 * 
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class TextEntityRenderer implements RendererPlugin<TextEntity> {

    @Override
    public Class<?> getObjectClass() {
        return TextEntity.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, TextEntity e) {
        g.setColor(e.color);
        g.setFont(e.font);
        g.drawString(e.text, (int) e.position.x, (int) e.position.y);
    }
}
