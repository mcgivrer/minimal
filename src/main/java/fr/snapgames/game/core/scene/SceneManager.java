package fr.snapgames.game.core.scene;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.config.OldConfiguration;

/**
 * <p>The {@link SceneManager} intends to activate one of multiple {@link Scene} instances according
 * to the required game play.</p>
 * <p>The {@link Scene} implementations are define into the <code>/game.properties</code> file
 * under the keys <code>game.scene.list</code> and <code>game.scene.default</code>.</p>
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 */
public class SceneManager {
    private Game game;
    /**
     * List of scenes instances
     */
    private final Map<String, Scene> scenes = new HashMap<>();
    /**
     * List of available implementations
     */
    private final Map<String, Class<? extends Scene>> availableScenes = new HashMap<>();
    /**
     * The parent {@link Game}'s {@link OldConfiguration} instance.
     */
    private OldConfiguration config;

    private Scene activeScene;

    /**
     * Create the SceneManager with a parent Game instance.
     *
     * @param g the parent Game for this service.
     */
    public SceneManager(Game g) {
        game = g;
    }

    /**
     * <p>Initialize the service, taking configuration value from the {@link OldConfiguration} class.
     * Load all {@link Scene}'s implementation listed in to the <code>game.scene.list</code> configuration key.</p>
     * <p>After loaded and store all class implementation into the internal scene available list,
     * activates the default {@link Scene}, defined in to the configuration key <code>game.scene.default</code>.</p>
     * <p>
     * A sample scenes configuration entries in the <code>game.properties</code> configuration file could be :
     *
     * <pre>
     * game.scene.list=\
     *   demo:my.own.project.package.DemoScene,\
     *   title:my.own.project.package.TitleScene
     * game.scene.default=title
     * </pre>
     * <p>
     * Here are a list of 2 Scene's implementations, one identified by `demo`, other one by `title`, and the first to be
     * activated by default is `title`.
     * <p>
     * The list of Scene's instances of the key `game.scene.list` is :
     * <pre><code>{[key]:[full.name.of.implementation.Class](,)}</code></pre>
     *
     * <blockquote><em><strong>NOTE</strong> These {@link Scene} implementation would be some {@link AbstractScene}
     * extended implementation to take benefits from already pre-implemented scene management processing</em></blockquote>
     *
     * @param g The parent Game instance.
     * @see Scene
     * @see AbstractScene
     */
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

        if (!scenesList.isEmpty()) {
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
    }

    /**
     * Add a Scene implementation class to the manager.
     *
     * @param s the Scene implementation to be added.
     */
    private void add(Scene s) {
        scenes.put(s.getName(), s);
    }

    /**
     * Default Scene activation according to the
     */
    public void activateDefaultScene() {
        String defaultSceneName = config.getString("game.scene.default", "demo");
        activate(defaultSceneName);
    }

    /**
     * Activate a specific Scene from the scenes list. if not already instantiated,
     * do it and store in the available internal scene instances list.
     * The newly instantiated {@link Scene} is initialized, resources are loaded and the creation is requested.
     * <ol>
     *     <li>Instantiate the scene class with its constructor (e.g.: <code>MyScene(myGame)</code>),</li>
     *     <li>Call its {@link Scene#initialize(Game)},</li>
     *     <li>Load resources with {@link Scene#loadResources(Game)},</li>
     *     <li>And create the scene with the {@link Scene#create(Game)}.</li>
     * </ol>
     *
     * @param name the name of the {@link Scene} implementation to be activated
     * @see Scene
     * @see AbstractScene
     */
    public void activate(String name) {
        if (!scenes.containsKey(name) && availableScenes.containsKey(name)) {
            Class<? extends Scene> sceneClass = availableScenes.get(name);
            if (Optional.ofNullable(activeScene).isPresent()) {
                activeScene.dispose(game);
            }
            try {
                Scene s = sceneClass.getConstructor(Game.class, String.class).newInstance(game, name);
                add(s);
                s.initialize(game);
                s.loadResources(game);
                s.create(game);
                this.activeScene = s;
                System.out.printf("SceneManager:Scene %s instance has been activated%n", sceneClass.getName());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                System.err.printf("SceneManager:Unable to create Scene %s instance:%s%n", sceneClass.getName(), e.getMessage());
            }
        } else {
            System.err.printf(
                    "SceneManager:The Scene %s does not exists in configuration file for key '%s'.%n",
                    name, name);
            System.err.printf(
                    "SceneManager:Known scenes are '%s'.%n", scenes.entrySet().toString());
        }

    }


    /**
     * Get the current active {@link Scene}.
     *
     * @return the current active {@link Scene} instance.
     */
    public Scene getActiveScene() {
        return activeScene;
    }
}
