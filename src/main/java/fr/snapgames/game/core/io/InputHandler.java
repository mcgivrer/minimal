package fr.snapgames.game.core.io;

import fr.snapgames.game.core.Game;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Internal Input listener.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 */
public class InputHandler implements ActionListener {
    Game game;
    private Map<Integer, KeyEvent> events = new ConcurrentHashMap<>();
    private List<ActionListener> listeners = new CopyOnWriteArrayList<>();
    private boolean ctrlDown;
    private boolean shiftDown;
    private boolean altDown;
    private boolean altGrDown;

    public InputHandler(Game g) {
        this.game = g;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (Optional.ofNullable(game).isPresent()) {
            checkMetaKeys(e);
            listeners.forEach(kl -> kl.keyPressed(e));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        checkMetaKeys(e);
        events.put(e.getKeyCode(), e);
        listeners.forEach(kl -> kl.keyPressed(e));
    }

    private void checkMetaKeys(KeyEvent e) {
        ctrlDown = e.isControlDown();
        shiftDown = e.isShiftDown();
        altDown = e.isAltDown();
        altGrDown = e.isAltGraphDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        checkMetaKeys(e);
        events.remove(e.getKeyCode());
        listeners.forEach(kl -> kl.keyReleased(e));

    }

    public boolean getKey(int code) {
        return (events.containsKey(code));
    }

    public void addListener(ActionListener keyListener) {
        listeners.add(keyListener);
    }

    public void removeListener(ActionListener keyListener) {
        listeners.remove(keyListener);
    }

    public boolean isCtrlPressed() {
        return ctrlDown;
    }

    public boolean isShiftPressed() {
        return shiftDown;
    }

    public boolean isAltPressed() {
        return altDown;
    }

    public boolean isAltGrPressed() {
        return altGrDown;
    }
}
