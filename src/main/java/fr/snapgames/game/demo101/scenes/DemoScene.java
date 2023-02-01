package fr.snapgames.game.demo101.scenes;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.*;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.graphics.plugins.ParticlesEntityRenderer;
import fr.snapgames.game.core.math.*;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.AbstractScene;
import fr.snapgames.game.demo101.scenes.behaviors.CoinBehavior;
import fr.snapgames.game.demo101.scenes.behaviors.RainEffectBehavior;
import fr.snapgames.game.demo101.scenes.io.DemoListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @author Frédéric Delorme
 **/
public class DemoScene extends AbstractScene {

    private BufferedImage playerImg;
    private BufferedImage backgroundImg;
    private BufferedImage coinImg;

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

        g.getInputHandler().addListener(new DemoListener(g, this));

        //Add Background Image
        GameEntity backgroundImage = new GameEntity("backgroundImage")
                .setImage(backgroundImg)
                .setLayer(0)
                .setPriority(1);
        add(backgroundImage);

        // Add a score display
        int viewportWidth = config.getInteger("game.camera.viewport.width", 320);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(viewportWidth - 80, 25))
                .setColor(Color.WHITE)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(2)
                .setPriority(3)
                .stickToCamera(true)
                .addBehavior(new Behavior<TextEntity>() {
                    @Override
                    public void update(Game game, TextEntity entity, double dt) {
                        GameEntity p = getEntity("player");
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
                .setImage(playerImg)
                .setColor(Color.BLUE)
                .setMaterial(Material.RUBBER)
                .setAttribute("maxVelocity", 10.0)
                .setAttribute("maxAcceleration", 8.0)
                .setAttribute("speedStep", 0.4)
                .setMass(8.0)
                .setLayer(2)
                .setPriority(1)
                .addBehavior(new Behavior<GameEntity>() {
                    @Override
                    public void update(Game game, GameEntity entity, double dt) {

                    }

                    @Override
                    public void input(Game game, GameEntity entity) {
                        double accel = (Double) entity.getAttribute("speedStep", 1.0);
                        accel = inputHandler.isShiftPressed() ? accel * 4.0 : accel;
                        accel = inputHandler.isCtrlPressed() ? accel * 2.0 : accel;

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
        add(createRain("rain", 500, world));
        // define Camera to track player.
        int vpWidth = config.getInteger("game.camera.viewport.width", 320);
        int vpHeight = config.getInteger("game.camera.viewport.height", 200);

        Camera cam = new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight));
        renderer.setCurrentCamera(cam);
    }

    private ParticlesEntity createRain(String entityName, int nbParticles, World world) {
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
                    .setMass(0.1)
                    .setMaterial(Material.AIR);
            pes.getChild().add(p);
        }
        return pes;
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
                    .setMass(5.0)
                    .setLayer(4)
                    .setPriority(4)
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
    public void draw(Game g, Renderer r) {

    }

    @Override
    public void dispose(Game g) {

    }
}
