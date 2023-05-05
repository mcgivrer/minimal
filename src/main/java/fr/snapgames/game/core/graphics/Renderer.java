package fr.snapgames.game.core.graphics;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.plugins.*;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.scene.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Renderer service to draw every GameEntity on screen.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class Renderer {
    private List<BufferedImage> buffers = new ArrayList<>();
    private Configuration config;
    private Game game;
    private Color clearColor = Color.BLACK;
    private double scale;

    private Map<Class<?>, RendererPlugin<?>> plugins = new HashMap<>();

    public Renderer(Game g, Dimension bufferSize) {
        this.game = g;
        this.config = game.getConfiguration();
        // initialize the 2 image Buffers
        this.buffers.clear();
        this.buffers.add(new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB));
        this.buffers.add(new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB));

        // Add required renderer plugins
        addPlugin(new GameEntityRenderer());
        addPlugin(new TextEntityRenderer());
        addPlugin(new ParticlesEntityRenderer());
        addPlugin(new LightRenderer());
        addPlugin(new InfluencerRenderer());
    }


    private static int compare(GameEntity e1, GameEntity e2) {
        return e1.getLayer() == e2.getLayer()
                ? e1.getPriority() == e2.getPriority() ? 0
                : Integer.compare(e1.getPriority(), e2.getPriority())
                : Integer.compare(e1.getLayer(), e2.getLayer());
    }

    public void addPlugin(RendererPlugin<?> rendererPlugin) {
        this.plugins.put(rendererPlugin.getObjectClass(), rendererPlugin);
    }

    public void draw(Scene scene, Map<String, Object> stats) {
        draw(0, scene, stats);
    }

    public void draw(int bufferId, Scene scene, Map<String, Object> stats) {
        BufferedImage buffer = buffers.get(bufferId);
        if (Optional.ofNullable(buffer).isPresent()) {
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // clear scene
            g.setColor(clearColor);
            g.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            Camera currentCamera = scene.getActiveCamera();
            // draw all entities according to Camera
            scene.getEntities().values().stream()
                    .filter(e -> e.isActive() && isInViewPort(currentCamera, e))
                    .sorted(Renderer::compare)
                    .forEach(entity -> {
                        // draw Scene
                        if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                            currentCamera.preDraw(g);
                        }
                        drawEntity(g, entity);
                        for (Behavior b : entity.behaviors) {
                            b.draw(game, g, entity);
                        }
                        if (Optional.ofNullable(currentCamera).isPresent() && !entity.isStickToCamera()) {
                            currentCamera.postDraw(g);
                        }
                    });
            if (game.getDebug() > 0) {
                drawDebugGrid(scene, g, 32);
                if (Optional.ofNullable(currentCamera).isPresent()) {
                    drawCameraDebug(g, currentCamera);
                }
                if (game.getDebug() > 2) {
                    drawEntitesDebug(scene, g);
                }
            }
            g.dispose();
        }
        // remove inactive object.
        scene.getEntities().values().stream()
                .filter(e -> !e.isActive())
                .toList()
                .forEach(ed -> scene.getEntities().remove(ed.getName()));
    }

    private boolean isInViewPort(Camera currentCamera, GameEntity e) {

        if (currentCamera != null) {
            if (e.isStickToCamera()
                    || (e.box.getBounds2D().getWidth() > currentCamera.viewport.getWidth()
                    && e.box.getBounds2D().getHeight() > currentCamera.viewport.getHeight())) {
                return true;
            } else {
                return currentCamera.viewport.intersects(e.box.getBounds2D());
            }
        }
        return true;
    }

    public void drawEntity(Graphics2D g, GameEntity entity) {
        entity.setDrawnBy(null);
        if (plugins.containsKey(entity.getClass())) {
            RendererPlugin rp = plugins.get(entity.getClass());
            g.rotate(entity.rotation, entity.position.x + entity.size.x * 0.5, entity.position.y + entity.size.y * 0.5);
            rp.draw(this, g, entity);
            g.rotate(-entity.rotation, entity.position.x + entity.size.x * 0.5, entity.position.y + entity.size.y * 0.5);
            entity.setDrawnBy(rp.getClass());
        } else {
            System.err.printf("Renderer:Unknown rendering plugin for Entity class %s%n", entity.getClass().getName());
        }
        entity.getChild().forEach(c -> drawEntity(g, c));
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDebugGrid(Scene scene, Graphics2D g, int step) {
        World world = game.getPhysicEngine().getWorld();
        g.setFont(g.getFont().deriveFont(8.5f));

        Camera currentCamera = scene.getActiveCamera();
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
        g.drawRect(0, 0, world.getPlayArea().width, world.getPlayArea().height);
    }

    public void drawEntitesDebug(Scene scene, Graphics2D g) {
        Camera currentCamera = scene.getActiveCamera();
        scene.getEntities().values().stream()
                .filter(e -> e.isActive() && isInViewPort(currentCamera, e))
                .sorted(Renderer::compare)
                .forEach(v -> {
                    if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                        currentCamera.preDraw(g);
                    }
                    if (plugins.containsKey(v.getClass())) {
                        RendererPlugin rp = ((RendererPlugin) plugins.get(v.getClass()));
                        rp.drawDebug(this, g, v);
                    } else {
                        System.err.printf("Renderer:Unknown rendering plugin for Entity class %s%n",
                                v.getClass().getName());
                    }

                    if (Optional.ofNullable(currentCamera).isPresent() && !v.isStickToCamera()) {
                        currentCamera.postDraw(g);
                    }
                });
    }

    private void drawCameraDebug(Graphics2D g, Camera camera) {
        g.drawRect(10, 10, (int) camera.viewport.getWidth() - 20, (int) camera.viewport.getHeight() - 20);
        g.drawString(String.format("cam: %s", camera.getName()), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("targ: %s", camera.target.getName()), 20, 44);
    }


    public void reset() {
        buffers.clear();
    }

    public BufferedImage getBuffer() {
        return this.buffers.get(0);
    }

    public BufferedImage getBuffer(int bufferId) {
        return this.buffers.get(bufferId);
    }

    public BufferedImage getOutputBuffer() {
        return buffers.get(0);
    }
}
