package fr.snapgames.game.core.scene;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.config.Configuration;

public class SceneManager {
    private Game game;
    /**
     * List of scenes instances
     */
    private Map<String, Scene> scenes = new HashMap<>();
    /**
     * List of available implementations
     */
    private Map<String, Class<? extends Scene>> availableScenes = new HashMap<>();
    /**
     * The parent {@link Game}'s {@link Configuration} instance.
     */
    private Configuration config;

    private Scene activeScene;

    public SceneManager(Game g) {
        game = g;
    }

    public void initialize(Game g) {
        this.game = g;
        config = g.getConfiguration();
        String listOfScene = config.getString("game.scene.list", "");
        List<String> scenesList;
        if (listOfScene.contains(",")) {
            scenesList = Arrays.asList(config.getString("game.scene.list", "").split(","));
        } else {
            scenesList = new ArrayList<>();
            scenesList.add(listOfScene);
        }

        if (Optional.ofNullable(scenesList).isPresent() && !scenesList.isEmpty()) {
            scenesList.forEach(s -> {
                String[] kv = s.split(":");
                try {
                    Class<? extends Scene> sceneToAdd = (Class<? extends Scene>) Class.forName(kv[1]);
                    availableScenes.put(kv[0], sceneToAdd);
                    System.out.printf("Add scene %s:%s", kv[0], kv[1]);
                } catch (ClassNotFoundException e) {
                    System.err.printf("Unable to load class %s", kv[1]);
                }
            });
        }
        // initialize default scene.
        activateDefaultScene();
    }

    public void add(Scene s) {
        scenes.put(s.getName(), s);
    }

    public void activateDefaultScene() {
        String defaultSceneName = config.getString("game.scene.default", "demo");
        activate(defaultSceneName);
    }

    public void activate(String name) {
        if (!scenes.containsKey(name) && availableScenes.containsKey(name)) {
            Class<? extends Scene> sceneClass = availableScenes.get(name);
            try {
                Scene s = sceneClass.getConstructor(Game.class, String.class).newInstance(game, name);
                scenes.put(name, s);
                s.initialize(game);
                s.create(game);
                this.activeScene = s;
                this.activeScene.initialize(game);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                System.out.printf("Unable to create Scene {0} instance:" + e.getMessage(), sceneClass.getName());
            }
        } else {
            System.err.printf(
                    "The Scene %s does not exists in configuration file for key '%s'.%n",
                    name, name);
        }

    }


    public Scene getActiveScene() {
        return activeScene;
    }
}
