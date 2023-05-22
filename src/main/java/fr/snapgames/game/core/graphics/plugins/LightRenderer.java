package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.Light;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class LightRenderer implements RendererPlugin<Light> {
    @Override
    public Class<?> getObjectClass() {
        return Light.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, Light e) {
        Light l = (Light) e;
        Composite oldComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, l.getIntensity()));
        switch (l.getLightType()) {
            case SPOT -> {
                l.colors = new Color[]{
                        l.color,
                        new Color(0.0f, 0.0f, 0.0f, 0.0f)};
                final RadialGradientPaint rgp = new RadialGradientPaint(
                        new Point((int) l.position.x, (int) l.position.y),
                        (float) l.size.x,
                        l.dist, l.colors);
                g.setPaint(rgp);
                g.fill(new Ellipse2D.Double(l.position.x - l.size.x, l.position.y - l.size.y, l.size.x * 2,
                        l.size.y * 2));
            }
            case CONE -> {
                // TODO implement the CONE light type
            }
            case AMBIENT -> {
                g.setColor(l.color);
                g.fillRect(0, 0, (int) l.size.x, (int) l.size.y);
            }
        }
        g.setComposite(oldComp);
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, Light e, double scale) {
        g.setColor(Color.ORANGE);
        if (e.getType() == EntityType.CIRCLE) {
            g.translate(-e.box.getBounds2D().getWidth() * 0.5 * scale, -e.box.getBounds2D().getHeight() * 0.5 * scale);
        }
        g.draw(e.collisionBox);
        if (e.getType() == EntityType.CIRCLE) {
            g.translate(e.box.getBounds2D().getWidth() * 0.5 * scale, e.box.getBounds2D().getHeight() * 0.5 * scale);
        }
    }
}


