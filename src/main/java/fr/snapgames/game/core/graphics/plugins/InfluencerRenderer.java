package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.Influencer;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Optional;

/**
 * InfluencerRenderer plugin to draw Influencer on screen.
 *
 * @author Frédéric Delorme
 * @since 0.0.4
 **/
public class InfluencerRenderer implements RendererPlugin<Influencer> {
    @Override
    public Class<?> getObjectClass() {
        return Influencer.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, Influencer e) {
        switch (e.type) {
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
                break;
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
                break;
            }
            default -> {
                System.err.printf("Unkown how to draw this Influcener instance %s%n.", e.getName());
            }
        }
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, Influencer v) {
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
