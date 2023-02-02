package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.Light;

import java.awt.*;
import java.util.Optional;

public class LightBehavior implements Behavior<Light> {
    @Override
    public void update(Game game, Light entity, double dt) {
        entity.setIntensity(entity.getIntensity() * entity.getDeltaIntensity());
        switch (entity.getLightType()) {
            case CONE -> {
                if (Optional.ofNullable(entity.getTarget()).isPresent()) {
                    // to be implemented soon !
                }
            }
            case SPOT -> {
                if (Optional.ofNullable(entity.getTarget()).isPresent()) {
                    entity.setPosition(entity.getTarget().position);
                }
            }
            case AMBIANT -> {
                // Nothing special to change for this type of light.
            }
        }

    }

    @Override
    public void input(Game game, Light entity) {

    }

    @Override
    public void draw(Game game, Graphics2D g, Light entity) {

    }
}
