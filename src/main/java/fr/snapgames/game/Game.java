package fr.snapgames.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main Game Java2D test.
 *
 * @author Frédéric Delorme
 * @since 2022
 */
public class Game extends JPanel {

    /**
     * Internal Class to manipulate simple Vector2D.
     *
     * @author Frédéric Delorme
     */
    public static class Vector2D {
        public double x, y;

        public Vector2D() {
            x = 0.0f;
            y = 0.0f;
        }

        /**
         * @param x
         * @param y
         */
        public Vector2D(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        public Vector2D add(Vector2D v) {
            return new Vector2D(x + v.x, y + v.y);
        }

        public Vector2D substract(Vector2D v1) {
            return new Vector2D(x - v1.x, y - v1.y);
        }

        public Vector2D multiply(double f) {
            return new Vector2D(x * f, y * f);
        }

        public double dot(Vector2D v1) {

            return v1.x * y + v1.y * x;
        }

        public double length() {
            return Math.sqrt(x * x + y * y);
        }

        public double distance(Vector2D v1) {
            return substract(v1).length();
        }

        public Vector2D divide(double f) {
            return new Vector2D(x / f, y / f);
        }

        public Vector2D normalize() {
            return divide(length());
        }

        public Vector2D negate() {
            return new Vector2D(-x, -y);
        }

        public double angle(Vector2D v1) {
            double vDot = this.dot(v1) / (this.length() * v1.length());
            if (vDot < -1.0)
                vDot = -1.0;
            if (vDot > 1.0)
                vDot = 1.0;
            return Math.acos(vDot);

        }

        public Vector2D addAll(List<Vector2D> forces) {
            Vector2D sum = new Vector2D();
            for (Vector2D f : forces) {
                sum = sum.add(f);
            }
            return sum;
        }

        public String toString() {
            return String.format("{x:%04.2f,y:%04.2f}", x, y);
        }

        public Vector2D maximize(double maxAccel) {
            if (Math.abs(x) > maxAccel) {
                x = Math.signum(x) * maxAccel;
            }
            if (Math.abs(y) > maxAccel) {
                y = Math.signum(y) * maxAccel;
            }
            return this;
        }
    }

    public static class World {
        private Dimension playArea;
        private Vector2D gravity;

        public World(Dimension area, Vector2D gravity) {
            this.playArea = area;
            this.gravity = gravity;
        }

        public Vector2D getGravity() {
            return this.gravity;
        }

        public Dimension getPlayArea() {
            return playArea;
        }

        public boolean isNotContaining(GameEntity ge) {
            return ge.position.x < 0
                    || ge.position.x + ge.size.x > playArea.width
                    || ge.position.y < 0
                    || ge.position.y + ge.size.y > playArea.height;
        }
    }

    /**
     * Camera used to see/follow entity in game viewport.
     *
     * @author Frédéric Delorme
     */
    public static class Camera {
        public String name;
        public Vector2D position;
        public GameEntity target;
        public double rotation = 0.0f, tween = 0.0f;
        public Dimension viewport;

        public Camera(String name) {
            this.name = name;
            position = new Vector2D(0, 0);
            target = null;
        }

        public Camera setTarget(GameEntity t) {
            this.target = t;
            return this;
        }

        public Camera setViewport(Dimension dim) {
            this.viewport = dim;
            return this;
        }

        public Camera setRotation(double r) {
            this.rotation = r;
            return this;
        }

        public Camera setTween(double tween) {
            this.tween = tween;
            return this;
        }

        public void preDraw(Graphics2D g) {
            g.translate(-position.x, -position.y);
            g.rotate(-rotation);
        }

        public void postDraw(Graphics2D g) {

            g.rotate(rotation);
            g.translate(position.x, position.y);
        }

        public void update(double dt) {

            this.position.x += Math
                    .ceil((target.position.x + (target.size.x * 0.5) - ((viewport.width) * 0.5) - this.position.x)
                            * tween * Math.min(dt, 10));
            this.position.y += Math
                    .ceil((target.position.y + (target.size.y * 0.5) - ((viewport.height) * 0.5) - this.position.y)
                            * tween * Math.min(dt, 10));
        }
    }

    public interface Behavior {
        void update(Game game, GameEntity entity, double dt);

        void input(Game game, GameEntity entity);

        void draw(Game game, GameEntity entity, Graphics2D g);
    }

    public enum EntityType {
        RECTANGLE,
        CIRCLE,
        IMAGE;
    }

    /**
     * Entity manipulated by Game.
     *
     * @author Frédéric Delorme
     */
    public static class GameEntity {
        public String name = "noname";
        public Vector2D position = new Vector2D(0, 0);
        public Vector2D speed = new Vector2D(0, 0);
        public Vector2D acceleration = new Vector2D(0, 0);
        public Vector2D size = new Vector2D(16, 16);
        public EntityType type = EntityType.RECTANGLE;
        public boolean stickToCamera = false;
        public double elasticity = 1.0;
        public double roughness = 1.0;
        public double rotation = 0.0;
        public List<Vector2D> forces = new ArrayList<>();
        public Color color = Color.RED;
        public Map<String, Object> attributes = new HashMap<>();
        public List<Behavior> behaviors = new ArrayList<>();
        public BufferedImage image;

        /**
         * Create a new GameEntity with a name.
         *
         * @param name Name of the new entity.
         */
        public GameEntity(String name) {
            this.name = name;
            attributes.put("maxSpeed", 8.0);
            attributes.put("maxAcceleration", 3.0);
            attributes.put("mass", 10.0);
        }

        public void update(Game g, double dt) {
            for (Behavior b : behaviors) {
                b.update(g, this, dt);
            }
            if (!isStickToCamera()) {
                this.acceleration = this.acceleration.addAll(this.forces);
                this.acceleration = this.acceleration.multiply((double) attributes.get("mass"));

                this.acceleration.maximize((double) attributes.get("maxAcceleration"));

                this.speed = this.speed.add(this.acceleration.multiply(dt)).multiply(roughness);
                this.speed.maximize((double) attributes.get("maxSpeed"));

                this.position = this.position.add(this.speed.multiply(dt));
                this.forces.clear();
            }
        }

        public void draw(Graphics2D g) {
            switch (type) {
                case IMAGE:
                    if (Optional.ofNullable(image).isPresent()) {
                        boolean direction = speed.x > 0;
                        if (direction) {
                            g.drawImage(image, (int) position.x, (int) position.y, null);
                        } else {
                            g.drawImage(image, (int) (position.x + size.x), (int) position.y, (int) -size.x, (int) size.y, null);
                        }
                    }
                    break;
                case RECTANGLE:
                    g.setColor(color);
                    g.fillRect((int) position.x, (int) position.y, (int) size.x, (int) size.y);
                    break;
                case CIRCLE:
                    g.setColor(color);
                    g.setPaint(color);
                    g.fill(new Ellipse2D.Double(position.x, position.y, size.x, size.y));
                    break;
            }
        }

        public GameEntity setPosition(Vector2D pos) {
            this.position = pos;
            return this;
        }

        public GameEntity stickToCamera(boolean flag) {
            this.stickToCamera = flag;
            return this;
        }

        public boolean isStickToCamera() {
            return stickToCamera;
        }

        public GameEntity setSize(Vector2D s) {
            this.size = s;
            return this;
        }

        public GameEntity setType(EntityType t) {
            this.type = t;
            return this;
        }

        public GameEntity setImage(BufferedImage i) {
            this.image = i;
            return this;
        }


        public GameEntity setSpeed(Vector2D speed) {
            this.speed = speed;
            return this;
        }

        public GameEntity setRoughness(double r) {
            this.roughness = r;
            return this;
        }

        public GameEntity setElasticity(double e) {
            this.elasticity = e;
            return this;
        }

        public Collection<String> getDebugInfo() {
            List<String> ls = new ArrayList<>();
            ls.add(String.format("name:%s", name));
            ls.add(String.format("pos: %04.2f,%04.2f", this.position.x, this.position.y));
            ls.add(String.format("spd: %04.2f,%04.2f", this.speed.x, this.speed.y));
            ls.add(String.format("acc: %04.2f,%04.2f", this.acceleration.x, this.acceleration.y));
            return ls;
        }

        public GameEntity setAttribute(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        public GameEntity setColor(Color color) {
            this.color = color;
            return this;
        }

        public GameEntity addBehavior(Behavior b) {
            this.behaviors.add(b);
            return this;
        }

        public Object getAttribute(String attrName, Object defaultValue) {
            return attributes.getOrDefault(attrName, defaultValue);
        }
    }

    public static class TextEntity extends GameEntity {

        public String text;
        public Font font;

        /**
         * Create a new TextEntity with a name.
         *
         * @param name Name of the new entity.
         */
        public TextEntity(String name) {
            super(name);
        }

        public TextEntity setText(String text) {
            this.text = text;
            return this;
        }

        public TextEntity setFont(Font font) {
            this.font = font;
            return this;
        }

        public void draw(Graphics2D g) {
            g.setColor(color);
            g.setFont(font);
            g.drawString(text, (int) position.x, (int) position.y);
        }

        @Override
        public Collection<String> getDebugInfo() {
            Collection<String> l = super.getDebugInfo();
            l.add(String.format("txt:%s", text));
            return l;
        }
    }

    /**
     * Configuration oad a properties file and
     * let user gather converted value to
     * <ul>
     *     <li>Integer,</li>
     *     <li>Double,</li>
     *     <li>Boolean,</li>
     *     <li>.</li>
     * </ul>
     * from these properties.
     * The user can also {@link Configuration#save()} values after changes.
     */
    public static class Configuration {
        private final Properties parameters = new Properties();
        String filePath;

        public Configuration(String file) {
            this.filePath = file;
            try {
                parameters.load(Game.class.getResourceAsStream(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getInteger(String key, int defaultValue) {
            if (parameters.containsKey(key)) {
                return Integer.parseInt(parameters.getProperty(key));
            }
            return defaultValue;
        }

        public double getDouble(String key, double defaultValue) {
            if (parameters.containsKey(key)) {
                return Double.parseDouble(parameters.getProperty(key));
            }
            return defaultValue;
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            if (parameters.containsKey(key)) {
                return Boolean.parseBoolean(parameters.getProperty(key));
            }
            return defaultValue;
        }

        public String getString(String key, String defaultValue) {
            if (parameters.containsKey(key)) {
                return parameters.getProperty(key);
            }
            return defaultValue;
        }

        public void parseArguments(String[] args) {
            for (String s : args) {
                String[] p = s.split("=");
                String key = p[0];
                String value = p[1];
                if (parameters.containsKey(s)) {
                    parameters.setProperty(key, value);
                }
            }
        }

        public void save() {
            StringWriter fw = new StringWriter();
            try {
                parameters.store(fw, "updated From CommandLine");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static class I18n {
        private static final ResourceBundle messages = ResourceBundle.getBundle("i18n.messages");

        public static String get(String key) {
            return messages.getString(key);
        }

        public static String get(String key, Object... args) {
            return String.format(messages.getString(key), args);
        }
    }

    /**
     * Internal Input listener.
     *
     * @author Frédéric Delorme
     */
    public static class Input implements KeyListener {
        Game game;
        Map<Integer, KeyEvent> events = new ConcurrentHashMap<>();

        public Input(Game g) {
            this.game = g;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (Optional.ofNullable(game).isPresent()) {
                game.keyTyped(e);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            events.put(e.getKeyCode(), e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            events.remove(e.getKeyCode());
        }

        public boolean getKey(int code) {
            return (events.containsKey(code));
        }

    }


    // Frames to be rendered
    double FPS = 60.0;
    double fpsDelay = 1000000.0 / 60.0;
    double scale = 2.0;
    // some internal flags
    boolean debug = true;
    boolean exit = false;
    boolean pause = false;

    /**
     * the Test mode is a flag to deactivate the while Loop in the {@link Game#loop} method.
     */
    private boolean testMode;

    // Internal components
    BufferedImage buffer;
    Color clearColor = Color.BLACK;
    Configuration config;
    I18n i18n;
    JFrame frame;
    Input input;
    // Internal GameEntity cache
    Map<String, GameEntity> entities = new HashMap<>();
    World world;
    Camera currentCamera = null;

    public Game() {
        this("/game.properties", false);
    }

    public Game(String configFilePath, boolean testMode) {

        this.testMode = testMode;
        config = new Configuration(configFilePath);

        debug = config.getBoolean("game.debug", false);

        FPS = config.getDouble("game.screen.fps", 60.0);
        fpsDelay = 1000000.0 / FPS;

        input = new Input(this);
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

        frame.addKeyListener(input);
        frame.pack();

        frame.setVisible(true);
        if (frame.getBufferStrategy() == null) {
            frame.createBufferStrategy(config.getInteger("game.buffer.strategy", 2));
        }
        buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Initialize game.
     *
     * @param args Java command line arguments
     */
    private void initialize(String[] args) {
        config.parseArguments(args);
        create((Graphics2D) frame.getGraphics());
    }

    private void create(Graphics2D g) {

        int worldWidth = config.getInteger("game.world.width", 1000);
        int worldHeight = config.getInteger("game.world.height", 1000);

        world = new World(new Dimension(worldWidth, worldHeight),
                new Vector2D(0, -0.981)
        );

        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("Score")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(worldWidth - 80, 25))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.WHITE)
                .stickToCamera(true);
        add(score);

        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setSize(new Vector2D(16, 16))
                .setColor(Color.BLUE)
                .setRoughness(1.0)
                .setElasticity(0.21)
                .setAttribute("maxSpeed", 6.0)
                .setAttribute("maxAcceleration", 2.0)
                .setAttribute("mass", 8.0)
                .addBehavior(new Behavior() {
                    @Override
                    public void update(Game game, GameEntity entity, double dt) {

                    }

                    @Override
                    public void input(Game game, GameEntity entity) {
                        double accel = (Double) entity.getAttribute("speedStep", 1.0);
                        if (input.getKey(KeyEvent.VK_ESCAPE)) {
                            game.exit = true;
                        }

                        if (input.getKey(KeyEvent.VK_UP)) {
                            entity.forces.add(new Vector2D(0, -accel));
                        }
                        if (input.getKey(KeyEvent.VK_DOWN)) {
                            entity.forces.add(new Vector2D(0, accel));
                        }
                        if (input.getKey(KeyEvent.VK_RIGHT)) {
                            entity.forces.add(new Vector2D(accel, 0));
                        }
                        if (input.getKey(KeyEvent.VK_LEFT)) {
                            entity.forces.add(new Vector2D(-accel, 0));
                        }
                    }

                    @Override
                    public void draw(Game game, GameEntity entity, Graphics2D g) {

                    }
                });
        add(player);

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
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 2.0)
                    .addBehavior(new Behavior() {
                        @Override
                        public void input(Game g, GameEntity e) {

                        }

                        @Override
                        public void draw(Game game, GameEntity e, Graphics2D g) {

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
                        }

                        ;
                    });

            add(e);
        }

        int vpWidth = config.getInteger("game.viewport.width", 320);
        int vpHeight = config.getInteger("game.viewport.height", 200);

        Camera cam = new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight));
        setCurrentCamera(cam);
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
        double scale = config.getDouble("game.screen.scale", 2.0);
        if (Optional.ofNullable(buffer).isPresent()) {
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // clear scene
            g.setColor(clearColor);
            g.clearRect(0, 0, this.getWidth(), this.getHeight());


            for (GameEntity entity : entities.values()) {
                // draw Scene
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.preDraw(g);
                }
                for (Behavior b : entity.behaviors) {
                    b.draw(this, entity, g);
                }
                entity.draw(g);
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.postDraw(g);
                }
            }
            if (debug) {
                drawDebugGrid(g, 32);
            }
            if (Optional.ofNullable(currentCamera).isPresent() && debug) {
                drawCameraDebug(g, currentCamera);
            }
            if (pause) {
                g.setColor(new Color(0.3f, 0.6f, 0.4f, 0.9f));
                g.fillRect(0, (currentCamera.viewport.height - 24) / 2, currentCamera.viewport.width, 24);
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));
                String pauseTxt = I18n.get("game.state.pause.message");
                int lng = g.getFontMetrics().stringWidth(pauseTxt);
                g.drawString(pauseTxt, (currentCamera.viewport.width - lng) / 2, (currentCamera.viewport.height + 12) / 2);
            }
            g.dispose();
            // draw image to screen.
            if (Optional.ofNullable(frame).isPresent()) {
                if (frame.getBufferStrategy() != null) {
                    if (frame.getBufferStrategy().getDrawGraphics() == null) {
                        return;
                    }
                    Graphics2D g2 = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();

                    g2.scale(scale, scale);
                    g2.drawImage(buffer, 0, 18,
                            null);
                    g2.scale(1.0 / scale, 1.0 / scale);
                    if (debug) {
                        g2.setColor(Color.ORANGE);

                        g2.setFont(g2.getFont().deriveFont(11.0f));
                        g2.drawString("FPS:" + realFPS, 40, 50);
                    }
                    g2.dispose();
                    if (frame.getBufferStrategy() != null) {
                        frame.getBufferStrategy().show();
                    }
                }
            }
        }
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDebugGrid(Graphics2D g, int step) {

        g.setFont(g.getFont().deriveFont(8.0f));

        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.preDraw(g);
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < world.getPlayArea().getWidth(); x += step) {
            g.drawLine(x, 0, x, (int) world.getPlayArea().getHeight());
        }
        for (int y = 0; y < world.getPlayArea().getHeight(); y += step) {
            g.drawLine(0, y, (int) world.getPlayArea().getWidth(), y);
        }
        g.setColor(Color.CYAN);
        g.drawRect(0, 0, (int) world.getPlayArea().getWidth(), (int) world.getPlayArea().getHeight());
        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.postDraw(g);
        }

        g.setColor(Color.ORANGE);
        entities.forEach((k, v) -> {
            if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                currentCamera.preDraw(g);
            }

            g.drawRect((int) v.position.x, (int) v.position.y,
                    (int) v.size.x, (int) v.size.y);
            int il = 0;
            for (String s : v.getDebugInfo()) {
                g.drawString(s, (int) (v.position.x + v.size.x + 4.0), (int) v.position.y + il);
                il += 10;
            }
            if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                currentCamera.postDraw(g);
            }

        });
        g.drawRect(0, 0, world.getPlayArea().width, world.getPlayArea().height);
    }

    private void drawCameraDebug(Graphics2D g, Camera camera) {
        g.drawRect(10, 10, camera.viewport.width - 20, camera.viewport.height - 20);
        g.drawString(String.format("cam: %s", camera.name), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("targ: %s", camera.target.name), 20, 44);
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
        for (GameEntity entity : entities.values()) {
            entity.forces.add(world.getGravity().negate());
            entity.update(this, elapsed);
            constrainEntityToWorld(world, entity);
        }
        currentCamera.update(elapsed);
    }

    /**
     * Constrain the GameEntity ge to stay in the world play area.
     *
     * @param world the defne World for the Game
     * @param ge    the GameEntity to be checked against world's play area constrains
     * @see GameEntity
     * @see World
     */
    private void constrainEntityToWorld(World world, GameEntity ge) {
        if (world.isNotContaining(ge)) {
            if (ge.position.x + ge.size.x > world.getPlayArea().width) {
                ge.position.x = world.getPlayArea().width - ge.size.x;
            }
            if (ge.position.x < 0) {
                ge.position.x = 0;
            }
            if (ge.position.y + ge.size.y > world.getPlayArea().height) {
                ge.position.y = world.getPlayArea().height - ge.size.y;
            }
            if (ge.position.y < 0) {
                ge.position.y = 0;
            }
            ge.speed = ge.speed.multiply(-ge.elasticity);
        }
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


    private void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
            this.pause = !this.pause;
        }
    }

    public Map<String, GameEntity> getEntities() {
        return entities;
    }

    public void setCurrentCamera(Camera cam) {
        this.currentCamera = cam;
    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }


    public void setWorld(World world) {
        this.world = world;
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
