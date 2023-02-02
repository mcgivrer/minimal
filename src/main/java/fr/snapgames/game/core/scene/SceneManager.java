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
                    System.out.printf("SceneManager:Add scene %s:%s%n", kv[0], kv[1]);
                } catch (ClassNotFoundException e) {
                    System.err.printf("SceneManager:Unable to load class %s%n", kv[1]);
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
                s.loadResources(game);
                s.create(game);
                this.activeScene = s;
                System.out.printf("SceneManager:Scene %s instance has been activated%n", sceneClass.getName());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                System.err.printf("SceneManager:Unable to create Scene %S instance:%s%n", sceneClass.getName(), e.getMessage());
            }
        } else {
            System.err.printf(
                    "SceneManager:The Scene %s does not exists in configuration file for key '%s'.%n",
                    name, name);
            System.err.printf(
                    "SceneManager:Known scenes are '%s'.%n", scenes.entrySet().toString());
        }

    }


    public Scene getActiveScene() {
        return activeScene;
    }
}
