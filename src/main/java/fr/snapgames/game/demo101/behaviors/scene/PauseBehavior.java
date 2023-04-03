package fr.snapgames.game.demo101.behaviors.scene;

import java.awt.Graphics2D;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.scene.Scene;

public class PauseBehavior implements Behavior<Scene> {
    private final GameEntity entity;

    public PauseBehavior(GameEntity pauseEntity) {
        this.entity = pauseEntity;
    }

    @Override
    public void update(Game game, Scene entity, double dt) {
        this.entity.setActive(game.isUpdatePause());
    }
}
