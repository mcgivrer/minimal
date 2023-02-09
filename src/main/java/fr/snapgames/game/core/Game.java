package fr.snapgames.game.core;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.GameKeyListener;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.core.scene.SceneManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
    double scale = 2.0;
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
    private JFrame frame;

    // all the services.
    private InputHandler inputHandler;
    private Renderer renderer;
    private PhysicEngine physicEngine;
    private SceneManager scm;

    /**
     * Create Game by loading configuration from the default game.properties file, with no test mode activated.
     *
     * @see Game#Game(String, boolean)
     */
    public Game() {
        this("/game.properties", false);
    }

    /**
     * This the entry point of our {@link Game}, providing a path to a configuration file to take in account to configure all the Game engine,
     * and set a flag to activate or not a specific test mode.
     *
     * @param configFilePath the path to the *.properties configuration file.
     * @param mode           if true, set the Game in test mode and the GameLoop will not occur:
     *                       only ONE loop in the game loop will be achieved, to let the unit test manage
     *                       the looping strategy for test purpose.
     * @see Configuration
     */
    public Game(String configFilePath, boolean mode) {
        this.testMode = testMode;
        config = new Configuration(configFilePath);
        debug = config.getInteger("game.debug", 0);
        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        createFrame();

        // create services
        renderer = new Renderer(this);
        physicEngine = new PhysicEngine(this);
        scm = new SceneManager(this);
        scm.initialize(this);
    }

    /**
     * Create the Window where the magic happen !
     * It gathers the required window size parameters from the translation file and configuration file with 4 main keys:
     *
     * <ul>
     *     <li><code>game.title</code> from the I18n file, the title of the window</li>
     *     <li><code>game.camera.viewport.width</code></li>
     *     <li><code>game.camera.viewport.height</code></li>
     *     <li><code>game.screen.scale</code></li>
     *     <li><code>game.buffer.strategy</code></li>
     * </ul>
     *
     * @see Configuration
     * @see I18n
     */
    private void createFrame() {
        inputHandler = new InputHandler(this);
        inputHandler.addListener(new GameKeyListener(this));

        String title = I18n.get("game.window.title");
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double scale = config.getDouble("game.screen.scale", 2.0);
        int width = (int) (scale * config.getInteger("game.camera.viewport.width", 320));
        int height = (int) (scale * config.getInteger("game.camera.viewport.height", 200));
        Dimension dim = new Dimension(width, height);

        // define Window content and size.
        frame.setLayout(new GridLayout());

        frame.setContentPane(this);

        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.setMinimumSize(dim);
        frame.setMaximumSize(dim);
        frame.setIconImage(ResourceManager.loadImage("/images/sg-logo-image.png"));

        setBackground(Color.BLACK);
        frame.setIgnoreRepaint(true);
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setLocationByPlatform(false);
        // define Window content and size.
        frame.setLayout(new GridLayout());
        getLayout().layoutContainer(frame);

        frame.setContentPane(this);
        frame.getContentPane().setPreferredSize(dim);

        frame.addKeyListener(inputHandler);
        frame.pack();

        frame.setVisible(true);
        if (frame.getBufferStrategy() == null) {
            frame.createBufferStrategy(config.getInteger("game.buffer.strategy", 2));
        }
    }

    /**
     * Initialize game.
     *
     * @param args Java command line arguments
     */
    private void initialize(String[] args) {
        config.parseArguments(args);
        scm.activateDefaultScene();
        create((Graphics2D) frame.getGraphics());

    }

    private void create(Graphics2D g) {
        Scene s = scm.getActiveScene();
        s.loadResources(this);
    }

    /**
     * Draw all things on screen.
     *
     * @param realFPS displayed Frame Per Seconds.
     * @param realUPS
     */
    private void draw(long realFPS, long realUPS) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("fps", realFPS);
        stats.put("ups", realUPS);
        renderer.draw(stats);
    }

    /**
     * update game entities according to input
     */
    private void input() {
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
        dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
        config.save();
    }

    /**
     * Main Game loop
     */
    public void loop() {
        // elapsed Game Time
        double start = 0;
        double end = 0;
        double dt = 0;
        // FPS measure
        long frames = 0;
        long realFPS = 0;

        long ups = 0;
        long realUPS = 0;
        long timeFrame = 0;
        while (!exit && !testMode) {
            start = System.nanoTime() / 1000000.0;
            input();
            if (!pause) {
                update(dt * .04);
                ups += 1;
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1000) {
                realFPS = frames;
                frames = 0;
                realUPS = ups;
                ups = 0;
                timeFrame = 0;
            }

            draw(realFPS, realUPS);
            waitUntilStepEnd(dt);

            end = System.nanoTime() / 1000000.0;
            dt = end - start;
        }

    }

    private void waitUntilStepEnd(double dt) {
        if (dt < fpsDelay) {
            try {
                Thread.sleep((long) (fpsDelay - dt) / 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
            this.pause = !this.pause;
        }
    }

    public int getDebug() {
        return debug;
    }

    public boolean isUpdatePause() {
        return pause;
    }

    public void setExit(boolean b) {
        exit = true;
    }

    public JFrame getFrame() {
        return frame;
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

}
