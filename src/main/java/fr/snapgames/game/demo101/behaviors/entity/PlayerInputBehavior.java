package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.event.KeyEvent;

/**
 * Manage input foe player entity, according to key pressed, some forces ({@link Vector2D})
 * are applied to the {@link GameEntity} entity.
 *
 * @author Frédéric Delorme
 * @since 0.06
 */
public class PlayerInputBehavior implements Behavior<GameEntity> {

    @Override
    public void input(Game game, GameEntity entity) {
        InputHandler inputHandler = game.getInputHandler();
        double accel = (Double) entity.getAttribute("speedStep", 0.02);
        accel = inputHandler.isShiftPressed() ? accel * 2.0 : accel;
        accel = inputHandler.isCtrlPressed() ? accel * 1.5 : accel;

        if (inputHandler.getKey(KeyEvent.VK_UP)) {
            entity.forces.add(new Vector2D(0, -accel * 3.0));
        }
        if (inputHandler.getKey(KeyEvent.VK_DOWN)) {
            entity.forces.add(new Vector2D(0, accel));
        }
        if (inputHandler.getKey(KeyEvent.VK_RIGHT)) {
            entity.setDirection(1);
            entity.forces.add(new Vector2D(accel, 0));
        }
        if (inputHandler.getKey(KeyEvent.VK_LEFT)) {
            entity.setDirection(-1);
            entity.forces.add(new Vector2D(-accel, 0));
        }
    }
}