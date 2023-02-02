package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.ParticlesEntity;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;

/**
 * ParticlesEntity renderer olugin
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public class ParticlesEntityRenderer implements RendererPlugin<ParticlesEntity> {
    @Override
    public Class<?> getObjectClass() {
        return ParticlesEntity.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, ParticlesEntity pe) {
        pe.getChild().forEach(e -> {
            r.drawEntity(g, e);
        });
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, ParticlesEntity e) {

    }
}
