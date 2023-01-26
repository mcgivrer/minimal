package fr.snapgames.game.core;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.GameKeyListener;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.core.scene.SceneManager;
import fr.snapgames.game.demo101.scenes.DemoScene;

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
    private BufferedImage buffer;
    private Color clearColor = Color.BLACK;
    private Configuration config;
    private I18n i18n;
    private JFrame frame;
    private InputHandler inputHandler;
    // Internal GameEntity cache
    private Map<String, GameEntity> entities = new HashMap<>();
    private Renderer renderer;
    private PhysicEngine pe;
    private SceneManager scm;

    public Game() {
        this("/game.properties", false);
    }

    public Game(String configFilePath, boolean testMode) {
        this.testMode = testMode;
        config = new Configuration(configFilePath);
        debug = config.getInteger("game.debug", 0);
        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        createFrame();

        // create services
        renderer = new Renderer(this);
        pe = new PhysicEngine(this);
        scm = new SceneManager(this);
        scm.initialize(this);
    }

    private void createFrame() {
        inputHandler = new InputHandler(this);
        inputHandler.addListener(new GameKeyListener(this));
        String title = I18n.get("game.title");
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double scale = config.getDouble("game.screen.scale", 2.0);
        int width = (int) (scale * config.getInteger("game.screen.width", 320));
        int height = (int) (scale * config.getInteger("game.screen.height", 200));
        Dimension dim = new Dimension(width, height);

        // define Window content and size.
        frame.setLayout(new GridLayout());

        frame.setContentPane(this);

        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.setMinimumSize(dim);
        frame.setMaximumSize(dim);

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

        // add entities to the services.
        renderer.addEntities(entities.values());
        pe.addEntities(entities.values());

        scm.activateDefaultScene();

        create((Graphics2D) frame.getGraphics());

    }

    private void create(Graphics2D g) {
        Scene s = scm.getActiveScene();
        s.loadResources(this);
        s.create(this);
    }

    public void add(GameEntity e) {
        entities.put(e.name, e);
    }

    /**
     * Draw all things on screen.
     *
     * @param realFPS displayed Frame Per Seconds.
     */
    private void draw(long realFPS) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("fps", realFPS);
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
        pe.update(elapsed);
        renderer.getCurrentCamera().update(elapsed);
        scm.getActiveScene().update(this, elapsed);
    }

    /**
     * Request to close this Window frame.
     */
    public void close() {
        dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
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
        long timeFrame = 0;
        while (!exit && !testMode) {
            start = System.nanoTime() / 1000000.0;
            input();
            if (!pause) {
                update(dt * .04);
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1000) {
                realFPS = frames;
                frames = 0;
                timeFrame = 0;
            }

            draw(realFPS);
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
        close();
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
            this.pause = !this.pause;
        }
    }

    public Map<String, GameEntity> getEntities() {
        return entities;
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
        return pe;
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
