package fr.snapgames.game.demo101.scenes;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.AbstractScene;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
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
        pe.getWorld().setPlayArea(new Dimension(worldWidth, worldHeight));

        //Add Background Image
        GameEntity backgroundImage = new GameEntity("backgroundImage")
                .setImage(backgroundImg)
                .setLayer(0)
                .setPriority(1);
        add(backgroundImage);

        // Add a score display
        int viewportWidth = config.getInteger("game.camera.viewport.width", 320);
        TextEntity score = (TextEntity) new TextEntity("score")
                .setText("00000")
                .setFont(g.getFont().deriveFont(20.0f))
                .setPosition(new Vector2D(viewportWidth - 80, 25))
                .setColor(Color.WHITE)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setLayer(1)
                .setPriority(1)
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
                .setMaterial(Material.DEFAULT)
                .setAttribute("maxVelocity", 6.0)
                .setAttribute("maxAcceleration", 2.0)
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
        createEnemies(20, worldWidth, worldHeight);

        // define Camera to track player.
        int vpWidth = config.getInteger("game.camera.viewport.width", 320);
        int vpHeight = config.getInteger("game.camera.viewport.height", 200);

        Camera cam = new Camera("camera")
                .setTarget(player)
                .setTween(0.1)
                .setViewport(new Dimension(vpWidth, vpHeight));
        renderer.setCurrentCamera(cam);
    }

    /**
     * Create nb enemies in the world area delimited by worldWidth x worldHeight.
     *
     * @param nb          nb enemies to create
     * @param worldWidth  width of the world
     * @param worldHeight
     */
    private void createEnemies(int nb, int worldWidth, int worldHeight) {
        Behavior<GameEntity> enemyBehavior = new Behavior<GameEntity>() {
            @Override
            public void input(Game g, GameEntity e) {

            }

            @Override
            public void draw(Game game, Graphics2D g, GameEntity e) {
                if (game.getDebug() > 2) {
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
                GameEntity p = getEntity("player");
                double attrDist = (double) entity.attributes.get("attractionDistance");
                double attrForce = (double) entity.attributes.get("attractionForce");
                if (p.position.distance(entity.position.add(p.size.multiply(0.5))) < attrDist) {
                    Vector2D v = p.position.substract(entity.position);
                    entity.forces.add(v.normalize().multiply(attrForce));
                }
                if (p.position.distance(entity.position.add(p.size.multiply(0.75))) < entity.size.add(p.size)
                        .multiply(0.25).length()) {
                    entity.setActive(false);
                    int score = (int) p.getAttribute("score", 0);
                    score += 20;
                    p.setAttribute("score", score);

                }
            }
        };

        for (int i = 0; i < nb; i++) {
            GameEntity e = new GameEntity("en_" + i)
                    .setPosition(new Vector2D(Math.random() * worldWidth, Math.random() * worldHeight))
                    .setImage(coinImg)
                    .setMaterial(Material.DEFAULT)
                    .setMass(5.0)
                    .setLayer(1)
                    .setPriority(1)
                    .setAttribute("maxVelocity", 4.0)
                    .setAttribute("maxAcceleration", 5.0)
                    .setAttribute("attractionDistance", 80.0)
                    .setAttribute("attractionForce", 3.0)
                    .addBehavior(enemyBehavior);

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
