package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;
import java.util.Optional;

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
        g.setFont(e.font);
        e.size.x = g.getFontMetrics().stringWidth(e.text);
        e.size.y = g.getFontMetrics().getHeight();
        if (Optional.ofNullable(e.shadowColor).isPresent()) {
            g.setColor(e.shadowColor);
            for (int d = 0; d <= e.shadowWidth; d++) {
                g.drawString(e.text, (int) e.position.x + d, (int) e.position.y + d);
            }
        }
        if (Optional.ofNullable(e.borderColor).isPresent()) {
            g.setColor(e.borderColor);
            for (int c = -e.borderWidth; c <= e.borderWidth; c++) {
                for (int d = -e.borderWidth; d <= e.borderWidth; d++) {
                    g.drawString(e.text, (int) e.position.x + d, (int) e.position.y + c);
                }
            }
        }

        g.setColor(e.color);
        g.drawString(e.text, (int) e.position.x, (int) e.position.y);
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, TextEntity v) {
        g.setColor(Color.ORANGE);

        g.drawRect(
                (int) v.position.x,
                (int) v.position.y - (g.getFontMetrics().getHeight() + g.getFontMetrics().getAscent()),
                (int) v.size.x, (int) v.size.y);
        int il = 0;
        for (String s : v.getDebugInfo()) {
            g.drawString(s, (int) (v.position.x + v.size.x + 4.0), (int) v.position.y + il);
            il += 10;
        }

    }
}
