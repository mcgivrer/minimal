package fr.snapgames.game.core.scene.transition;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.scene.Scene;

import java.util.Map;

public interface Transition {

    String getName();

    long getDuration();

    void start(Scene src, Scene dst);

    void stop();

    void update(Game g, double dt);

    void draw(Game g, Renderer r, Map<String, Object> stats);
}
