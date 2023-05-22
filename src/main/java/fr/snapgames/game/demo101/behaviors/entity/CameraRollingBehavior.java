package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.io.InputHandler;

import java.awt.event.KeyEvent;

public class CameraRollingBehavior implements Behavior<Camera> {
    @Override
    public void input(Game game, Camera entity) {

        InputHandler inputHandler = game.getInputHandler();

        if (inputHandler.getKey(KeyEvent.VK_L)) {
            entity.setRotation(entity.rotation + 0.1);
        }
        if (inputHandler.getKey(KeyEvent.VK_M)) {
            entity.setRotation(entity.rotation - 0.1);
        }
        if (inputHandler.getKey(KeyEvent.VK_DELETE)) {
            entity.setRotation(0);
        }

    }
}
