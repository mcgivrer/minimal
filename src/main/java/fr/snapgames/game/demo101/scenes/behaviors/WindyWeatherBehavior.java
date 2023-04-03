package fr.snapgames.game.demo101.scenes.behaviors;

import java.awt.Graphics2D;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.scene.Scene;

/**
 * crete a random windy on the current action world object.
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 **/
public class WindyWeatherBehavior implements Behavior<Scene> {
    double minAngleDirection;
    double maxAngleDirection;
    double maxWindSpeed = 0.0;
    double maxChangeDelay = 20.0 * 1000.0;
    double internalTime = 0.0;
    Vector2D windForceTarget = new Vector2D();

    public WindyWeatherBehavior(double maxWindSpeed, double minAngleDirection, double maxAngleDirection, double maxChangeDelay) {
        this.maxWindSpeed = maxWindSpeed;
        this.maxChangeDelay = maxChangeDelay;
        this.minAngleDirection = minAngleDirection;
        this.maxAngleDirection = maxAngleDirection;
    }

    @Override
    public void update(Game game, Scene scene, double dt) {
        PhysicEngine pe = game.getPhysicEngine();
        World w = pe.getWorld();
        internalTime += dt;
        if (internalTime > maxChangeDelay * 50.0) {
            internalTime = 0.0;
            windForceTarget = new Vector2D((-(maxAngleDirection - minAngleDirection)
                    + Math.cos(Math.random() * (maxAngleDirection - minAngleDirection) * 2.0))
                    * Math.random() * maxWindSpeed, 0.0);
        }
        w.setWind(windForceTarget);
    }

    @Override
    public void input(Game game, Scene scene) {

    }

    @Override
    public void draw(Game game, Graphics2D g, Scene scene) {

    }
}
