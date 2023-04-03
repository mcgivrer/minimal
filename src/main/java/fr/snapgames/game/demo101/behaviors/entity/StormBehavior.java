package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.Light;

import java.awt.*;

/**
 * Simulate Storm lightning effect (WIP)
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
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
            delay -= dt * 100;
        }
        if (lightning >= 0 && lightningInterval > maxDelayLightning) {
            entity.setActive(true);
            lightning -= 1;
            lightningInterval = 0;
        } else {
            entity.setActive(false);
            lightningInterval += dt*100;
        }
    }
}
