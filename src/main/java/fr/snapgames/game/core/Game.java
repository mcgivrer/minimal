package fr.snapgames.game.core;

import fr.snapgames.game.core.audio.SoundSystem;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.gameloop.GameLoop;
import fr.snapgames.game.core.gameloop.impl.FixedFrameGameLoop;
import fr.snapgames.game.core.graphics.Animations;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.graphics.Window;
import fr.snapgames.game.core.io.GameKeyListener;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.core.scene.SceneManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main Game Java2D test.
 *
 * @author Frédéric Delorme
 * @since 2022
 */
public class Game extends JPanel {

    // Frames to be rendered
    double FPS = 60.0;
    double fpsDelay = 1000000.0 / 60.0;
    double scale = 1.0;
    // debug level
    int debug = 0;
    // some internal flags
    boolean exit = false;
    boolean pause = false;

    /**
     * the Test mode is a flag to deactivate the while Loop in the {@link Game#loop}
     * method.
     */
    private boolean testMode;

    // Internal components
    private Configuration config;

    private GameLoop gameLoop;

    private Window window;
    // all the services.
    private InputHandler inputHandler;
    private Renderer renderer;

    private PhysicEngine physicEngine;
    private final SoundSystem soundSystem;
    private SceneManager scm;
    private Animations animations;
    private boolean pauseAuthorized;

    /**
     * Create Game by loading configuration from the default game.properties file,
     * with no test mode activated.
     *
     * @see Game#Game(String, boolean)
     */
    public Game() {
        this("/game.properties", false);
    }

    /**
     * This the entry point of our {@link Game}, providing a path to a configuration
     * file to take in account to configure all the Game engine,
     * and set a flag to activate or not a specific test mode.
     *
     * @param configFilePath the path to the *.properties configuration file.
     * @param mode           if true, set the Game in test mode and the GameLoop
     *                       will not occur:
     *                       only ONE loop in the game loop will be achieved, to let
     *                       the unit test manage
     *                       the looping strategy for test purpose.
     * @see Configuration
     */
    public Game(String configFilePath, boolean mode) {
        this.testMode = testMode;
        config = new Configuration(ConfigAttribute.values())
                .setConfigurationFile("/game.properties")
                .parseConfigFile();
        soundSystem = new SoundSystem(this);
    }

    /**
     * Initialize game.
     *
     * @param args Java command line arguments
     */
    private void initialize(String[] args) {
        config.parseArgs(args);
        debug = (int) config.get(ConfigAttribute.DEBUG_LEVEL);
        FPS = (int) config.get(ConfigAttribute.RENDER_FPS);
        fpsDelay = 1000000.0 / FPS;

        // retrieve some Window parameters
        String title = I18n.get("game.window.title");
        Dimension dim = (Dimension) config.get(ConfigAttribute.WINDOW_SIZE);
        gameLoop = new FixedFrameGameLoop(this);

        // set input handlers
        inputHandler = new InputHandler(this);
        inputHandler.addListener(new GameKeyListener(this));

        scale = (double) config.get(ConfigAttribute.WINDOW_SCALE);

        // Create the output window.
        window = new Window(this, title, dim);
        window.add(inputHandler);

        // create services
        renderer = new Renderer(this, (Dimension) config.get(ConfigAttribute.VIEWPORT_SIZE));
        physicEngine = new PhysicEngine(this);
        scm = new SceneManager(this);
        scm.initialize(this);


        scm.activateDefaultScene();
        create(window.getGraphics());

    }

    private void create(Graphics2D g) {
        Scene s = scm.getActiveScene();
        s.loadResources(this);
    }

    /**
     * Draw all things on screen.
     *
     * @param stats a list of stats in a {@link Map} to be displayed in the debug
     *              bar.
     */
    public void draw(Map<String, Object> stats) {
        renderer.draw(stats);
        window.drawFrom(renderer, stats, scale);
    }

    /**
     * update game entities according to input
     */
    public void input() {
        Scene s = scm.getActiveScene();
        for (GameEntity e : s.getEntities().values()) {
            for (Behavior b : e.behaviors) {
                b.input(this, e);
            }
        }
        scm.getActiveScene().input(this, inputHandler);
    }

    /**
     * Update entities.
     *
     * @param elapsed elapsed time since previous call.
     */
    public void update(double elapsed) {
        physicEngine.update(elapsed);
        if (Optional.ofNullable(renderer.getCurrentCamera()).isPresent()) {
            renderer.getCurrentCamera().update(elapsed);
        }
        scm.getActiveScene().update(this, elapsed);
    }

    /**
     * Request to close this Window frame.
     */
    public void dispose() {
        window.close();
        config.save();
    }

    /**
     * Main Game loop
     */
    public void loop() {
        Map<String, Object> context = new ConcurrentHashMap<>();
        gameLoop.loop(context);

    }

    private void waitUntilStepEnd(double dt) {
        if (dt < fpsDelay) {
            try {
                Thread.sleep((long) (fpsDelay - dt) / 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.printf("ERROR: Unable to wait for %d ms: %s%n", fpsDelay - dt, e.getMessage());
            }
        }
    }

    /**
     * Main run method.
     *
     * @param args Command line arguments
     */
    public void run(String[] args) {
        initialize(args);
        loop();
        dispose();
    }

    public int getDebug() {
        return debug;
    }

    public boolean isUpdatePause() {
        return pause;
    }

    public void setExit(boolean requestExit) {
        exit = requestExit;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public PhysicEngine getPhysicEngine() {
        return physicEngine;
    }

    public SceneManager getSceneManager() {
        return scm;
    }

    public Animations getAnimations() {
        return this.animations;
    }

    /**
     * Entry point for executing game.
     *
     * @param args list of command line arguments
     */
    public static void main(String[] args) {

        Game game = new Game();
        game.run(args);
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public boolean isDebugGreaterThan(int minDebug) {
        return debug > minDebug;
    }

    public void requestPause(boolean pause) {
        this.pause = pause;
    }

    public SoundSystem getSoundSystem() {
        return soundSystem;
    }

    public boolean isPauseAuthorized() {
        return this.pauseAuthorized;
    }

    public void setPauseAutorized(boolean authorized) {
        this.pauseAuthorized = authorized;
    }

    public boolean isExitRequested() {
        return exit;
    }

    public boolean isTestMode() {
        return testMode;
    }
}
