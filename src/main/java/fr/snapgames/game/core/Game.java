package fr.snapgames.game.core;

import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.scene.SceneManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
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
    // some internal flags
    boolean debug = true;
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
        debug = config.getBoolean("game.debug", false);
        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        createFrame();

        // create services
        renderer = new Renderer(this);
        pe = new PhysicEngine(this);
        scm = new SceneManager(this);
    }

    private void createFrame() {
        inputHandler = new InputHandler(this);
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
        create((Graphics2D) frame.getGraphics());

        // add entities to the services.
        renderer.addEntities(entities.values());
        pe.addEntities(entities.values());
    }

    private void create(Graphics2D g) {

        // define world play area with constrains
        int worldWidth = config.getInteger("game.world.width", 1000);
        int worldHeight = config.getInteger("game.world.height", 1000);
        World world = new World(new Dimension(worldWidth, worldHeight),
                new Vector2D(0, -0.981));
        getPhysicEngine().setWorld(world);
        // Add a score display
        int viewportWidth = config.getInteger("game.camera.viewport.width", 320);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("00000")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(viewportWidth - 80, 25))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.WHITE)
                .stickToCamera(true)
                .addBehavior(new Behavior<TextEntity>() {
                    @Override
                    public void update(Game game, TextEntity entity, double dt) {
                        GameEntity p = game.entities.get("player");
                        int score = (int) p.getAttribute("score", 0);
                        entity.setText(String.format("%05d", score));
                    }

                    @Override
                    public void input(Game game, TextEntity entity) {

                    }

                    @Override
                    public void draw(Game game, Graphics2D g, TextEntity entity) {

                    }
                });
        add(score);
        // Create a player
        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.BLUE)
                .setRoughness(1.0)
                .setElasticity(0.21)
                .setAttribute("maxSpeed", 6.0)
                .setAttribute("maxAcceleration", 2.0)
                .setAttribute("mass", 8.0)
                .addBehavior(new Behavior<GameEntity>() {
                    @Override
                    public void update(Game game, GameEntity entity, double dt) {

                    }

                    @Override
                    public void input(Game game, GameEntity entity) {
                        double accel = (Double) entity.getAttribute("speedStep", 1.0);
                        if (inputHandler.getKey(KeyEvent.VK_ESCAPE)) {
                            game.exit = true;
                        }

                        if (inputHandler.getKey(KeyEvent.VK_UP)) {
                            entity.forces.add(new Vector2D(0, -accel));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_DOWN)) {
                            entity.forces.add(new Vector2D(0, accel));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_RIGHT)) {
                            entity.forces.add(new Vector2D(accel, 0));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_LEFT)) {
                            entity.forces.add(new Vector2D(-accel, 0));
                        }
                    }

                    @Override
                    public void draw(Game game, Graphics2D g, GameEntity entity) {

                    }
                });
        add(player);

        // Create enemies Entity.
        for (int i = 0; i < 10; i++) {
            GameEntity e = new GameEntity("en_" + i)
                    .setPosition(new Vector2D(Math.random() * worldWidth, Math.random() * worldHeight))
                    .setSize(new Vector2D(12, 12))
                    .setColor(Color.RED)
                    .setType(EntityType.CIRCLE)
                    .setRoughness(1.0)
                    .setElasticity(0.1)
                    .setAttribute("maxSpeed", 8.0)
                    .setAttribute("maxAcceleration", 2.5)
                    .setAttribute("mass", 5.0)
                    .setAttribute("attractionDistance", 40.0)
                    .setAttribute("attractionForce", 2.0)
                    .addBehavior(new Behavior<GameEntity>() {
                        @Override
                        public void input(Game g, GameEntity e) {

                        }

                        @Override
                        public void draw(Game game, Graphics2D g, GameEntity e) {
                            if (game.debug) {
                                double attrDist = (double) e.getAttribute("attractionDistance", 0);
                                if (attrDist > 0) {
                                    g.setColor(Color.YELLOW);
                                    Ellipse2D el = new Ellipse2D.Double(
                                            e.position.x - (attrDist), e.position.y - (attrDist),
                                            e.size.x + (attrDist * 2.0), e.size.y + (attrDist * 2.0));
                                    g.draw(el);
                                }
                            }
                        }

                        @Override
                        public void update(Game game, GameEntity entity, double dt) {
                            // if player near this entity less than distance (attrDist),
                            // a force (attrForce) is applied to entity to reach to player.
                            GameEntity p = game.entities.get("player");
                            double attrDist = (double) entity.attributes.get("attractionDistance");
                            double attrForce = (double) entity.attributes.get("attractionForce");
                            if (p.position.distance(entity.position.add(p.size.multiply(0.5))) < attrDist) {
                                Vector2D v = p.position.substract(entity.position);
                                entity.forces.add(v.normalize().multiply(attrForce));
                            }
                            if (p.position.distance(entity.position.add(p.size.multiply(0.5))) < entity.size.add(p.size)
                                    .multiply(0.25).length()) {
                                entity.setActive(false);
                                int score = (int) p.getAttribute("score", 0);
                                score += 20;
                                p.setAttribute("score", score);

                            }
                        }
                    });

            add(e);
        }

        // define Camera to track player.
        int vpWidth = config.getInteger("game.camera.viewport.width", 320);
        int vpHeight = config.getInteger("game.camera.viewport.height", 200);

        Camera cam = new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight));
        renderer.setCurrentCamera(cam);
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
        for (GameEntity e : entities.values()) {
            for (Behavior b : e.behaviors) {
                b.input(this, e);
            }
        }
    }

    /**
     * Update entities.
     *
     * @param elapsed elapsed time since previous call.
     */
    public void update(double elapsed) {
        pe.update(elapsed);
        renderer.getCurrentCamera().update(elapsed);
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

    public boolean getDebug() {
        return debug;
    }

    public boolean isUpdatePause() {
        return pause;
    }

    public Configuration getConfiguration() {
        return config;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public JFrame getFrame() {
        return frame;
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

}
