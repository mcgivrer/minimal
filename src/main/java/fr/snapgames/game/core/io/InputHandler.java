package fr.snapgames.game.core.io;

import fr.snapgames.game.core.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
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
    List<KeyListener> listeners = new ArrayList<>();

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
        listeners.forEach(kl -> kl.keyPressed(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        events.remove(e.getKeyCode());
        listeners.forEach(kl -> kl.keyReleased(e));

    }

    public boolean getKey(int code) {
        return (events.containsKey(code));
    }

    public void addListener(KeyListener keyListener) {
        listeners.add(keyListener);
    }
}
