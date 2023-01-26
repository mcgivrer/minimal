package fr.snapgames.game.core.graphics;

import fr.snapgames.game.core.*;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.World;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renderer service to draw every GameEntity on screen.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class Renderer {
    BufferedImage buffer;
    Configuration config;
    private Game game;
    private JFrame frame;
    private Color clearColor = Color.BLACK;
    private double scale;
    private Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    private Camera currentCamera;
    private Map<Class<?>, RendererPlugin<?>> plugins = new HashMap<>();

    public Renderer(Game g) {
        this.game = g;
        this.config = game.getConfiguration();
        this.scale = config.getDouble("game.screen.scale", 2.0);
        this.frame = game.getFrame();
        this.buffer = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        this.addPlugins(new GameEntityRenderer());
        this.addPlugins(new TextEntityRenderer());
    }

    public void addEntities(Collection<GameEntity> entities) {
        entities.stream().forEach(e -> this.entities.put(e.name, e));
    }

    public void addEntity(GameEntity e) {
        this.entities.put(e.name, e);
    }

    private void addPlugins(RendererPlugin<?> rendererPlugin) {
        this.plugins.put(rendererPlugin.getObjectClass(), rendererPlugin);
    }

    public void setCurrentCamera(Camera cam) {
        this.currentCamera = cam;
    }

    public void draw(Map<String, Object> stats) {
        if (Optional.ofNullable(buffer).isPresent()) {
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // clear scene
            g.setColor(clearColor);
            g.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            // draw all entities according to Camera
            entities.values().stream().filter(e -> e.isActive()).forEach(entity -> {
                // draw Scene
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.preDraw(g);
                }
                for (Behavior b : entity.behaviors) {
                    b.draw(game, g, entity);
                }
                drawEntity(g, entity);
                if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                    currentCamera.postDraw(g);
                }
            });
            if (game.getDebug() > 0) {
                drawDebugGrid(g, 32);
                if (Optional.ofNullable(currentCamera).isPresent()) {
                    drawCameraDebug(g, currentCamera);
                }
            }
            if (game.isUpdatePause()) {
                drawPauseMode(g);
            }
            g.dispose();
            // draw image to screen.
            drawToScreen(stats);
        }
    }

    private void drawPauseMode(Graphics2D g) {
        g.setColor(new Color(0.3f, 0.6f, 0.4f, 0.9f));
        g.fillRect(0, (currentCamera.viewport.height - 24) / 2, currentCamera.viewport.width, 24);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(14.0f).deriveFont(Font.BOLD));
        String pauseTxt = I18n.get("game.state.pause.message");
        int lng = g.getFontMetrics().stringWidth(pauseTxt);
        g.drawString(pauseTxt, (currentCamera.viewport.width - lng) / 2, (currentCamera.viewport.height + 12) / 2);
    }

    private void drawToScreen(Map<String, Object> stats) {
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
                if (game.getDebug() > 0) {
                    g2.setColor(Color.ORANGE);
                    long realFPS = (long) stats.get("fps");
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

    private void drawEntity(Graphics2D g, GameEntity entity) {
        if (plugins.containsKey(entity.getClass())) {
            ((RendererPlugin) plugins.get(entity.getClass())).draw(this, g, entity);
        } else {
            System.err.printf("Unknown rendering plugin for Entity class %s%n", entity.getClass().getName());
        }
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDebugGrid(Graphics2D g, int step) {
        World world = game.getPhysicEngine().getWorld();
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
        g.drawRect(0, 0,
                (int) world.getPlayArea().getWidth(),
                (int) world.getPlayArea().getHeight());
        if (Optional.ofNullable(currentCamera).isPresent()) {
            currentCamera.postDraw(g);
        }

        g.setColor(Color.ORANGE);
        entities.values().stream()
                .filter(e -> e.isActive())
                .forEach(v -> {
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

    public Camera getCurrentCamera() {
        return this.currentCamera;
    }

}
