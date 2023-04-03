package fr.snapgames.game.core.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An {@link ActionListener} inheriting from {@link KeyListener} to provide default empty method's implementation.
 *
 * @author Frédéric Delorme
 * @since 0.6.0
 */
public interface ActionListener extends KeyListener {

    @Override
    default void keyTyped(KeyEvent e) {
    }

    @Override
    default void keyPressed(KeyEvent e) {
    }

    @Override
    default void keyReleased(KeyEvent e) {
    }
}
