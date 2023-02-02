package fr.snapgames.game.demo101.scenes.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.Light;

import java.awt.*;

public class StormBehavior implements Behavior<Light> {
    private final int maxNbLightning;
    private final int maxDelayLightning;
    private double delay;
    private double lightning;
    private int lightningInterval;

    public StormBehavior(double startDelay, int maxNbLightning, int maxDelayLightning) {
        this.delay = startDelay;
        this.maxNbLightning = maxNbLightning;
        this.maxDelayLightning = maxDelayLightning;
    }

    @Override
    public void update(Game game, Light entity, double dt) {
        if (delay <= 0) {
            delay = Math.random() * 10000 + 2000;
            lightning = Math.random() * maxNbLightning;
        } else {
            delay -= dt;
        }
        if (lightning >= 0 && lightningInterval > maxDelayLightning) {
            lightning -= 1;
            lightningInterval = 0;
            entity.setDeltaIntensity((float) Math.random());
        } else {
            entity.setDeltaIntensity(1.0f);
            lightningInterval += dt;
        }
    }

    @Override
    public void input(Game game, Light entity) {

    }

    @Override
    public void draw(Game game, Graphics2D g, Light entity) {

    }
}
