package fr.snapgames.game.core.io;

import fr.snapgames.game.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal Input listener.
 *
 * @author Frédéric Delorme
 */
public class InputHandler implements KeyListener {
    Game game;
    Map<Integer, KeyEvent> events = new ConcurrentHashMap<>();

    public InputHandler(Game g) {
        this.game = g;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Optional.ofNullable(game).isPresent()) {
            game.keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        events.put(e.getKeyCode(), e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        events.remove(e.getKeyCode());
    }

    public boolean getKey(int code) {
        return (events.containsKey(code));
    }

}
