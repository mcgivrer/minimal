package fr.snapgames.game.core.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import fr.snapgames.game.core.Game;

public class SceneManager {
    private Game game;

    private Map<String, Scene> scenes = new HashMap<>();

    private Scene activeScene;

    public SceneManager(Game g) {
        game = g;
    }

    public void add(Scene s) {
        scenes.put(s.getName(), s);
    }

    public void activate(String name) {
        if (Optional.ofNullable(activeScene).isPresent()) {
            activeScene.dispose(game);
        }
        Scene s = scenes.get(name);
        s.initialize(game);
        s.create(game);
        activeScene = s;
    }

    public Scene getActiveScene() {
        return activeScene;
    }
}
