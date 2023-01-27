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
        g.setColor(e.color);
        g.setFont(e.font);
        if(Optional.ofNullable(e.shadowColor).isPresent()){
            g.setColor(e.shadowColor);
            for(int d=0;d<e.shadowWidth;d++){
                g.drawString(e.text, (int) e.position.x+d, (int) e.position.y+d);
            }
        }
        if(Optional.ofNullable(e.borderColor).isPresent()){
            g.setColor(e.borderColor);
            for(int d=0;d<e.borderWidth;d++){
                g.drawString(e.text, (int) e.position.x+d, (int) e.position.y+d);
            }
        }
        g.drawString(e.text, (int) e.position.x, (int) e.position.y);
    }
}
