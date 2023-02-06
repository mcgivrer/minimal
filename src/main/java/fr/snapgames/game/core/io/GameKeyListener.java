package fr.snapgames.game.core.io;

import fr.snapgames.game.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A Common listener to key to support Game operation like
 *
 * <lu>
 * <li><kbd>D</kbd> switching Debug display information level from 0 to 4</li>
 * </lu>.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class GameKeyListener implements KeyListener {

    private final Game game;

    public GameKeyListener(Game g) {
        this.game = g;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            int debug = game.getDebug() + 1 < 5 ? game.getDebug() + 1 : 0;
            game.setDebug(debug);
        }
    }
}
