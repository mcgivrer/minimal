package fr.snapgames.game.demo101.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.demo101.scenes.DemoScene;

import java.awt.*;

public class PauseBehavior implements Behavior<Scene> {
    private final GameEntity entity;

    public PauseBehavior(GameEntity pauseEntity) {
        this.entity = pauseEntity;
    }

    @Override
    public void update(Game game, Scene entity, double dt) {
        if (game.isUpdatePause()) {
            this.entity.setActive(true);
        } else {
            this.entity.setActive(false);
        }
    }

    @Override
    public void input(Game game, Scene entity) {

    }

    @Override
    public void draw(Game game, Graphics2D g, Scene entity) {

    }
}
