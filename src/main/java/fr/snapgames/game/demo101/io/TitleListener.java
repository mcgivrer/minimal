package fr.snapgames.game.demo101.io;

import fr.snapgames.game.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The {@link fr.snapgames.game.demo101.scenes.TitleScene} input handler to manage keys on the title page.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class TitleListener implements KeyListener {
    Game game;

    public TitleListener(Game g) {
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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                game.setExit(true);
            }
            case KeyEvent.VK_SPACE, KeyEvent.VK_ENTER -> {
                game.getSceneManager().activate("demo");
            }
            default -> {
                // nothing here !
            }
        }
    }
}
