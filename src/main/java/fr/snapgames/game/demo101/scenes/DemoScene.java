package fr.snapgames.game.demo101.scenes;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.behaviors.LightBehavior;
import fr.snapgames.game.core.entity.*;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.graphics.plugins.ParticlesEntityRenderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.*;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.AbstractScene;
import fr.snapgames.game.demo101.scenes.behaviors.*;
import fr.snapgames.game.demo101.scenes.io.DemoListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * The {@link DemoScene} implements in a demonstration purpose all the features available in the Mini'mal framework.
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 **/
public class DemoScene extends AbstractScene {

    private BufferedImage playerImg;
    private BufferedImage backgroundImg;
    private BufferedImage coinImg;
    private DemoListener demoListener;

    public DemoScene(Game g, String name) {
        super(g, name);
    }

    @Override
    public void initialize(Game g) {
        super.initialize(g);
        g.getRenderer().addPlugin(new ParticlesEntityRenderer());
    }

    @Override
    public void loadResources(Game g) {
        backgroundImg = ResourceManager.loadImage("/images/backgrounds/forest.jpg");
        playerImg = ResourceManager.loadImage("/images/sprites01.png").getSubimage(0, 0, 32, 32);
        coinImg = ResourceManager.loadImage("/images/tiles01.png").getSubimage(8 * 16, 6 * 16, 16, 16);

    }

    @Override
    public void create(Game g) {
        // define world play area with constrains
        int worldWidth = config.getInteger("game.world.width", 1008);
        int worldHeight = config.getInteger("game.world.height", 640);
        World world = pe.getWorld().setPlayArea(new Dimension(worldWidth, worldHeight));

        demoListener = new DemoListener(g, this);
        g.getInputHandler().addListener(demoListener);

        //Add Background Image
        GameEntity backgroundImage = new GameEntity("backgroundImage")
                .setImage(backgroundImg)
                .setPhysicType(PhysicType.STATIC)
                .setLayer(0)
                .setPriority(1);
        add(backgroundImage);

        // create Stars
        createStars("star", 500, world, false);

        // Add a score display
        int vpWidth = config.getInteger("game.camera.viewport.width", 320);
        int vpHeight = config.getInteger("game.camera.viewport.height", 200);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(vpWidth - 80, 35))
                .setColor(Color.WHITE)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(20)
                .setPriority(1)
                .stickToCamera(true)
                .addBehavior(new Behavior<TextEntity>() {
                    @Override
                    public void update(Game game, TextEntity entity, double dt) {
                        GameEntity p = getEntity("player");
                        if (Optional.ofNullable(p).isPresent()) {
                            int score = (int) p.getAttribute("score", 0);
                            entity.setText(String.format("%05d", score));
                        }
                    }

                    @Override
                    public void input(Game game, TextEntity entity) {

                    }

                    @Override
                    public void draw(Game game, Graphics2D g, TextEntity entity) {

                    }
                });
        add(score);

        TextEntity pauseText = (TextEntity) new TextEntity("pause")
                .setText(I18n.get("game.state.pause.message"))
                .setFont(g.getFont().deriveFont(20.0f))
                .setPhysicType(PhysicType.STATIC)
                .setPosition(new Vector2D(vpWidth * 0.5, vpHeight * 0.5))
                .setColor(Color.WHITE)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(20)
                .setPriority(1)
                .stickToCamera(true)
                .setActive(false);
        add(pauseText);

        // Create a player
        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(worldWidth / 2.0, worldHeight / 2.0))
                .setImage(playerImg)
                .setColor(Color.BLUE)
                .setMaterial(Material.RUBBER)
                .setAttribute("maxVelocity", 10.0)
                .setAttribute("maxAcceleration", 8.0)
                .setAttribute("speedStep", 0.4)
                .setMass(8.0)
                .setLayer(10)
                .setPriority(1)
                .addBehavior(new Behavior<GameEntity>() {
                    @Override
                    public void update(Game game, GameEntity entity, double dt) {

                    }

                    @Override
                    public void input(Game game, GameEntity entity) {
                        double accel = (Double) entity.getAttribute("speedStep", 0.02);
                        accel = inputHandler.isShiftPressed() ? accel * 2.0 : accel;
                        accel = inputHandler.isCtrlPressed() ? accel * 1.5 : accel;

                        if (inputHandler.getKey(KeyEvent.VK_UP)) {
                            entity.forces.add(new Vector2D(0, -accel * 3.0));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_DOWN)) {
                            entity.forces.add(new Vector2D(0, accel));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_RIGHT)) {
                            entity.setDirection(1);
                            entity.forces.add(new Vector2D(accel, 0));
                        }
                        if (inputHandler.getKey(KeyEvent.VK_LEFT)) {
                            entity.setDirection(-1);
                            entity.forces.add(new Vector2D(-accel, 0));
                        }
                    }

                    @Override
                    public void draw(Game game, Graphics2D g, GameEntity entity) {

                    }
                });
        add(player);

        // Create enemies Entity.
        createCoins("coin_", 20, world, new CoinBehavior());

        // create Rain effect with a ParticleEntity.
        createRain("rain", 200, world);

        // add an ambient light
        Light ambiantLight = (Light) new Light("ambiant", new Rectangle2D.Double(0, 0, worldWidth, worldHeight), 0.2f)
                .setColor(new Color(0.0f, 0.0f, 0.6f, 0.8f))
                .setLayer(2)
                .setPriority(1)
                .addBehavior(new LightBehavior())
                .addBehavior(new StormBehavior(500, 4, 50));
        add(ambiantLight);

        // add some spotlights
        createSpotLights("spot", 10, world);

        // define Camera to track player.
        Influencer water = (Influencer) new Influencer("water")
                .setType(EntityType.RECTANGLE)
                .setPosition(new Vector2D(0, worldHeight * 0.85))
                .setSize(new Vector2D(worldWidth, worldHeight * 0.15))
                .setColor(new Color(0.0f, 0.3f, 0.8f, 0.7f))
                .setBorderColor(Color.CYAN)
                .setBorderWidth(1)
                .setMaterial(Material.WATER)
                .addForce(world.getGravity().multiply(0.98))
                .setLayer(11)
                .setPriority(2);
        add(water);

        Camera cam = new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Rectangle2D.Double(0, 0, vpWidth, vpHeight));
        renderer.setCurrentCamera(cam);


        // add randomly wind.
        add(new WindyWeatherBehavior(20.0, 0.0, 0.3, 5.0));
        add(new PauseBehavior(pauseText));
    }

    private void createStars(String prefixEntityName, int nbStars, World world, boolean active) {
        Dimension starArea = new Dimension(world.getPlayArea().width, (int) (world.getPlayArea().height * 0.85));
        for (int i = 0; i < nbStars; i++) {
            GameEntity star = new GameEntity(prefixEntityName + "_" + i)
                    .setType(EntityType.CIRCLE)
                    .setPhysicType(PhysicType.STATIC)
                    .setPosition(RandomUtils.ramdomVector(starArea))
                    .setSize(new Vector2D(1.0, 1.0))
                    .setColor(Color.WHITE)
                    .setLayer(5)
                    .setPriority(1 + i)
                    .setActive(active);
            add(star);
        }
    }

    private void createSpotLights(String prefixEntityName, int nbLights, World world) {
        for (int i = 0; i < nbLights; i++) {
            Light l = (Light) new Light(prefixEntityName + "_" + i,
                    world.getPlayArea().width * Math.random(),
                    200.0 + (world.getPlayArea().height - 200) * Math.random(),
                    200.0 * Math.random(),
                    1.0f)
                    .setLayer(2)
                    .setPriority(1 + i)
                    .setColor(RandomUtils.randomColorMinMax(
                            0.6f, 1.0f,
                            0.6f, 1.0f,
                            0.6f, 1.0f,
                            0.6f, 1.0f))
                    .addBehavior(new LightBehavior());
            add(l);
        }
    }

    private void createRain(String entityName, int nbParticles, World world) {
        ParticlesEntity pes = (ParticlesEntity) new ParticlesEntity(entityName)
                .setPosition(new Vector2D(Math.random() * world.getPlayArea().getWidth(), 0.0))
                .setSize(new Vector2D(
                        world.getPlayArea().getWidth(),
                        world.getPlayArea().getHeight()))
                .setLayer(1)
                .setPriority(1)
                .addBehavior(new RainEffectBehavior(world, Color.CYAN));

        for (int i = 0; i < nbParticles; i++) {
            GameEntity p = new GameEntity(pes.name + "_" + i)
                    .setType(EntityType.CIRCLE)
                    .setPhysicType(PhysicType.DYNAMIC)
                    .setSize(new Vector2D(1.0, 1.0))
                    .setPosition(
                            new Vector2D(
                                    world.getPlayArea().getWidth() * Math.random(),
                                    world.getPlayArea().getHeight() * Math.random()))
                    .setColor(Color.CYAN)
                    .setLayer(1)
                    .setPriority(i)
                    .setMass(1.0)
                    .setMaterial(Material.AIR);
            pes.getChild().add(p);
        }
        add(pes);
    }

    /**
     * Create nb enemies in the world area delimited by worldWidth x worldHeight.
     *
     * @param nb    nb enemies to create
     * @param world a World object.
     */
    public void createCoins(String namePattern, int nb, World world, Behavior<?> b) {
        for (int i = 0; i < nb; i++) {
            GameEntity e = new GameEntity(namePattern + GameEntity.index)
                    .setPosition(new Vector2D(Math.random() * world.getPlayArea().getWidth(),
                            Math.random() * world.getPlayArea().getHeight()))
                    .setImage(coinImg)
                    .setMaterial(Material.SUPER_BALL)
                    .setMass(25.0)
                    .setLayer(4)
                    .setPriority(4 + i)
                    .setAttribute("maxVelocity", 4.0)
                    .setAttribute("maxAcceleration", 5.0)
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 3.0)
                    .setAttribute("value", (int) (Math.random() * 50.0) - 15)
                    .addBehavior(b);

            add(e);
        }
    }

    @Override
    public void input(Game g, InputHandler ih) {
        if (ih.getKey(KeyEvent.VK_ESCAPE)) {
            g.setExit(false);
            g.getSceneManager().activate("title");
        }
    }

    @Override
    public void draw(Game g, Renderer r) {

    }

    @Override
    public void dispose(Game g) {
        getEntities().clear();
        game.getPhysicEngine().reset();
        game.getRenderer().reset();
        if (Optional.ofNullable(demoListener).isPresent()) {
            g.getInputHandler().removeListener(demoListener);
        }
    }
}
