package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.Influencer;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class WaterEffectBehavior implements Behavior<Influencer> {
    private final double minWaterHeight;
    private final double maxWaterHeight;
    private double initialHeight;
    double internalTime = 0;
    double vWater = +0.01;
    double waterHeight = 0;

    public WaterEffectBehavior(double minWaterLevel, double maxWaterLevel, double waterStep) {
        this.minWaterHeight = minWaterLevel;
        this.maxWaterHeight = maxWaterLevel;
        this.vWater = waterStep;
    }


    @Override
    public void update(Game game, Influencer entity, double dt) {
        if (internalTime == 0.0) {
            this.waterHeight = this.minWaterHeight;
            this.initialHeight = entity.position.y;
        } else {
            if (this.waterHeight < this.minWaterHeight) {
                this.waterHeight = this.minWaterHeight;
                this.vWater = -this.vWater;
            }
            if (this.waterHeight > maxWaterHeight) {
                this.waterHeight = this.maxWaterHeight;
                this.vWater = -this.vWater;
            }
            this.waterHeight += this.vWater;
        }
        entity.position.y = initialHeight - this.waterHeight;
        entity.size.y = this.waterHeight;
        entity.forces.add(new Vector2D(0.0, -this.vWater * 0.005));
        entity.updateBox();
        internalTime += dt;
    }

    @Override
    public void draw(Game game, Graphics2D g, Influencer e) {
        switch (e.type) {
            case RECTANGLE -> {
                if (Optional.ofNullable(e.borderColor).isPresent()) {
                    Stroke b = g.getStroke();
                    g.setColor(e.borderColor);
                    g.setStroke(new BasicStroke(e.borderWidth));
                    g.drawLine(
                            (int) e.position.x, (int) e.position.y,
                            (int) (e.position.x + e.size.x), (int) e.position.y);
                    g.setStroke(b);
                }
                g.setColor(e.color);
                Rectangle2D rectWater = new Rectangle2D.Double(
                        e.position.x, e.position.y,
                        e.size.x, e.size.y);
                g.fill(rectWater);
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
                System.err.printf("ERROR: Unknown how to draw this Influencer instance %s%n.", e.getName());
            }
        }
    }
}
