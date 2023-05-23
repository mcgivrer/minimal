package fr.snapgames.game.demo101.scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.audio.SoundClip;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.behaviors.LightBehavior;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.Influencer;
import fr.snapgames.game.core.entity.Light;
import fr.snapgames.game.core.entity.ParticlesEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Animations;
import fr.snapgames.game.core.graphics.TextAlign;
import fr.snapgames.game.core.graphics.plugins.ParticlesEntityRenderer;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.RandomUtils;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.AbstractScene;
import fr.snapgames.game.demo101.behaviors.entity.*;
import fr.snapgames.game.demo101.behaviors.scene.PauseBehavior;
import fr.snapgames.game.demo101.behaviors.scene.WindyWeatherBehavior;
import fr.snapgames.game.demo101.io.DemoListener;

/**
 * The {@link DemoScene} implements in a demonstration purpose all the features
 * available in the Mini'mal framework.
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 **/
public class DemoScene extends AbstractScene {

    private BufferedImage playerImg;
    private BufferedImage backgroundImg;
    private BufferedImage coinImg;
    private DemoListener demoListener;

    private Animations animations;

    private SoundClip collectCoinSound;

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
        backgroundImg = ResourceManager.getImage("/images/backgrounds/forest.jpg");
        playerImg = ResourceManager.getImage("/images/sprites01.png").getSubimage(0, 0, 32, 32);
        coinImg = ResourceManager.getImage("/images/tiles01.png").getSubimage(8 * 16, 6 * 16, 16, 16);
        g.getSoundSystem().load("collectCoin", "/audio/sounds/collect-coin.wav");
        animations = new Animations("/animations.properties");

    }

    @Override
    public void create(Game g) {
        // define world play area with constrains
        Dimension playArea = (Dimension) config.get(ConfigAttribute.PLAY_AREA_SIZE);
        World world = pe.getWorld().setPlayArea(playArea);
        world.setMaterial(Material.AIR);

        demoListener = new DemoListener(g, this);
        g.getInputHandler().addListener(demoListener);

        // Add Background Image
        GameEntity backgroundImage = new GameEntity("backgroundImage")
                .setImage(backgroundImg)
                .setPhysicType(PhysicType.STATIC)
                .setLayer(0)
                .setPriority(0);
        add(backgroundImage);

        // create Stars
        createStars("star", 500, world, false);

        // Add a score display
        Dimension viewport = (Dimension) config.get(ConfigAttribute.VIEWPORT_SIZE);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(viewport.width - 80, 35))
                .setColor(Color.WHITE)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(12)
                .setPriority(20)
                .setStickToCamera(true)
                .addBehavior(new Behavior<TextEntity>() {
                    @Override
                    public void update(Game game, TextEntity entity, double dt) {
                        GameEntity p = getEntity("player");
                        if (Optional.ofNullable(p).isPresent()) {
                            int score = (int) p.getAttribute("score", 0);
                            entity.setText(String.format("%05d", score));
                        }
                    }
                });
        add(score);

        Font pauseFont = g.getFont().deriveFont(14.0f);
        String pauseStr = I18n.get("game.state.pause.message");
        int textWidth = g.getFontMetrics(pauseFont).stringWidth(pauseStr);
        int textHeight = g.getFontMetrics(pauseFont).getHeight();
        TextEntity pauseText = (TextEntity) new TextEntity("text-pause")
                .setText(pauseStr)
                .setFont(pauseFont)
                .setTextAlign(TextAlign.CENTER)
                .setPhysicType(PhysicType.STATIC)
                .setPosition(new Vector2D((viewport.width - textWidth) * 0.5, (viewport.height - textHeight) * 0.5))
                .setSize(new Vector2D(viewport.width, textHeight + 4))

                .setColor(Color.WHITE)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(20)
                .setPriority(1)
                .setStickToCamera(true)
                .setActive(false);
        add(pauseText);

        // Create a player
        GameEntity player = new GameEntity("player")
                .setPosition(new Vector2D(playArea.width / 2.0, playArea.height / 2.0))
                .setImage(playerImg)
                .setColor(Color.BLUE)
                .setMaterial(Material.RUBBER)
                .setAttribute("maxVelocity", 10.0)
                .setAttribute("maxAcceleration", 8.0)
                .setAttribute("speedStep", 2.0)
                .setMass(8.0)
                .setLayer(10)
                .setPriority(1)
                .addBehavior(new PlayerInputBehavior())
                .setAttribute("player_jump", -4.0 * 2.0)
                // define animations for the player Entity.
                .add("player_idle", animations.get("player_idle").setSpeed(0.6))
                .add("player_walk", animations.get("player_walk"))
                .add("player_fall", animations.get("player_fall"))
                .add("player_jump", animations.get("player_jump"));
        add(player);

        // Create enemies Entity.
        createCoins("coin_", 20, world, new CoinBehavior());

        // create Rain effect with a ParticleEntity.
        createRainParticlesEntity("rain", 200, world);

        // add an ambient light
        Light ambientLight = (Light) new Light("light-ambient",
                new Rectangle2D.Double(0, 0, playArea.width, playArea.height), 0.2f)
                .setColor(new Color(0.0f, 0.0f, 0.6f, 0.8f))
                .setLayer(12)
                .setPriority(1)
                .addBehavior(new LightBehavior())
                .addBehavior(new StormBehavior(500, 4, 50));
        add(ambientLight);

        Light thunderLight = (Light) new Light("light-thunderLight",
                new Rectangle2D.Double(0, 0, playArea.width, playArea.height), 0.9f)
                .setColor(Color.WHITE)
                .setLayer(12)
                .setPriority(1)
                .addBehavior(new LightBehavior())
                .addBehavior(new StormBehavior(500, 4, 50))
                .setActive(false);
        add(thunderLight);

        // add some spotlights
        createSpotLights("light-spot", 10, world);

        // define Camera to track player.
        Influencer water = (Influencer) new Influencer("influ-water")
                .setType(EntityType.RECTANGLE)
                .setPosition(new Vector2D(0, playArea.height))
                .setSize(new Vector2D(playArea.width, 120))
                .setColor(new Color(0.0f, 0.3f, 0.8f, 0.7f))
                .setBorderColor(Color.CYAN)
                .setBorderWidth(1)
                .setMaterial(Material.WATER)
                .addForce(world.getGravity().multiply(0.98))
                .setLayer(10)
                .setPriority(2)
                .addBehavior(new WaterEffectBehavior(0.0, 120.0, 0.4));
        add(water);

        Influencer wind = (Influencer) new Influencer("influ-magnetic-field")
                .setType(EntityType.RECTANGLE)
                .setPosition(new Vector2D(0, 0))
                .setSize(new Vector2D(playArea.width * .15, playArea.height))
                .setColor(new Color(0.9f, 0.7f, 0.1f, 0.5f))
                .setBorderColor(Color.GREEN)
                .setBorderWidth(1)
                .setMaterial(Material.AIR)
                .addForce(new Vector2D(-0.04, 0.0))
                .setLayer(10)
                .setPriority(1);
        add(wind);

        Camera cam = (Camera) new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Rectangle2D.Double(0, 0, viewport.width, viewport.height))
                .addBehavior(new CameraRollingBehavior());
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

    private void createRainParticlesEntity(String entityName, int nbParticles, World world) {
        ParticlesEntity pes = (ParticlesEntity) new ParticlesEntity(entityName)
                .setPosition(new Vector2D(Math.random() * world.getPlayArea().getWidth(), 0.0))
                .setSize(new Vector2D(
                        world.getPlayArea().getWidth(),
                        world.getPlayArea().getHeight()))
                .setLayer(12)
                .setPriority(0)
                .addBehavior(new RainEffectBehavior(world, Color.CYAN, nbParticles));
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
            GameEntity e = new GameEntity(namePattern + GameEntity.getIndex())
                    .setPosition(new Vector2D(Math.random() * world.getPlayArea().getWidth(),
                            Math.random() * world.getPlayArea().getHeight()))
                    .setImage(coinImg)
                    .setMaterial(Material.SUPER_BALL)
                    .setType(EntityType.CIRCLE)
                    .setMass(25.0)
                    .setLayer(4)
                    .setPriority(4 + i)
                    .setAttribute("maxVelocity", 4.0)
                    .setAttribute("maxAcceleration", 5.0)
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 2.0)
                    .setAttribute("value", (int) (Math.random() * 50.0) - 15)
                    .addBehavior(b);

            add(e);
        }
    }

    @Override
    public void dispose(Game g) {
        getEntities().clear();
        game.getPhysicEngine().reset();
        game.getRenderer().reset();
        game.getSoundSystem().stopAll();
        if (Optional.ofNullable(demoListener).isPresent()) {
            g.getInputHandler().removeListener(demoListener);
        }
    }
}
