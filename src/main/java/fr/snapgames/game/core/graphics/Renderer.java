package fr.snapgames.game.core.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.plugins.*;
import fr.snapgames.game.core.math.World;

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
    private Color clearColor = Color.BLACK;
    private double scale;
    private Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    private List<GameEntity> pipeline = new CopyOnWriteArrayList<>();

    private Font debugFont;
    private String debugFilter = "";
    private Camera currentCamera;
    private Map<Class<?>, RendererPlugin<?>> plugins = new HashMap<>();


    public Renderer(Game g, Dimension bufferSize) {
        this.game = g;
        this.config = game.getConfiguration();
        this.buffer = new BufferedImage(bufferSize.width, bufferSize.height, BufferedImage.TYPE_INT_ARGB);
        debugFont = buffer.createGraphics().getFont().deriveFont(8.5f);
        // Add required renderer plugins
        addPlugin(new GameEntityRenderer());
        addPlugin(new TextEntityRenderer());
        addPlugin(new ParticlesEntityRenderer());
        addPlugin(new LightRenderer());
        addPlugin(new InfluencerRenderer());
        debugFilter = config.get(ConfigAttribute.DEBUG_FILTER);
    }

    public void addEntities(Collection<GameEntity> entities) {
        entities.forEach(this::addEntity);
    }

    public void addEntity(GameEntity e) {

        this.entities.put(e.getName(), e);
        pipeline.add(e);
        pipeline.sort(Renderer::compare);
    }

    private static int compare(GameEntity e1, GameEntity e2) {
        return e1.getLayer() == e2.getLayer() ? (Integer.compare(e1.getPriority(), e2.getPriority()))
                : e1.getLayer() > e2.getLayer() ? 1 : -1;
    }

    public void addPlugin(RendererPlugin<?> rendererPlugin) {
        this.plugins.put(rendererPlugin.getObjectClass(), rendererPlugin);
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
            pipeline.stream()
                    .filter(e -> e.isActive() && isInViewPort(currentCamera, e))
                    .sorted((e1, e2) -> e1.getLayer() == e2.getLayer()
                            ? e1.getPriority() == e2.getPriority() ? 0
                            : Integer.compare(e1.getPriority(), e2.getPriority())
                            : Integer.compare(e1.getLayer(), e2.getLayer()))
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
            g.dispose();
        }
        // remove inactive object.
        entities.values().stream()
                .filter(e -> !e.isActive())
                .toList()
                .forEach(ed -> entities.remove(ed.getName()));
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
            g.rotate(entity.rotation,
                    entity.position.x + entity.size.x * 0.5,
                    entity.position.y + entity.size.y * 0.5);
            rp.draw(this, g, entity);
            g.rotate(-entity.rotation,
                    entity.position.x + entity.size.x * 0.5,
                    entity.position.y + entity.size.y * 0.5);
            entity.setDrawnBy(rp.getClass());
        } else {
            System.err.printf("ERROR: Renderer:Unknown rendering plugin for Entity class %s%n", entity.getClass().getName());
        }
        entity.getChild().forEach(c -> drawEntity(g, c));
    }

    /**
     * draw debug grid on viewport.
     *
     * @param g    Graphics API
     * @param step Step to draw for grid
     */
    private void drawDebugGrid(Graphics2D g, int step, double scale) {
        World world = game.getPhysicEngine().getWorld();
        g.setFont(g.getFont().deriveFont(8.5f));

        moveToCameraViewport(g, currentCamera, 1.0, -1, true);

        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < world.getPlayArea().getWidth(); x += step) {
            g.drawLine((int) (x * scale), 0, (int) (x * scale), (int) (world.getPlayArea().getHeight() * scale));
        }
        for (int y = 0; y < world.getPlayArea().getHeight(); y += step) {
            g.drawLine(0, (int) (y * scale), (int) (world.getPlayArea().getWidth() * scale), (int) (y * scale));
        }
        g.setColor(Color.CYAN);
        g.drawRect(
                0, 0,
                (int) (world.getPlayArea().width * scale),
                (int) (world.getPlayArea().height * scale));

    public void drawEntitesDebug(Graphics2D g) {
        entities.values().stream()
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
                        System.err.printf("ERROR: Renderer:Unknown rendering plugin for Entity class %s%n",
                                v.getClass().getName());
                    }
        moveToCameraViewport(g, currentCamera, 1.0, 1, true);

    }

    private void drawCameraDebug(Graphics2D g, Camera camera) {
        g.drawRect(10, 10, (int) camera.viewport.getWidth() - 20, (int) camera.viewport.getHeight() - 20);
        g.drawString(String.format("cam: %s", camera.name), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 20, 32);
        g.drawString(String.format("tgt: %s", camera.target.name), 20, 44);
    private void drawCameraDebug(Graphics2D g, Camera camera, double scale) {
        g.drawRect(20, 20,
                (int) ((camera.viewport.getWidth() - 20) * scale),
                (int) ((camera.viewport.getHeight() - 20) * scale));
        g.drawString(String.format("cam: %s", camera.getName()), 20, 20);
        g.drawString(String.format("pos: %04.2f,%04.2f", camera.position.x, camera.position.y), 30, 42);
        g.drawString(String.format("target: %s", camera.target.getName()), 30, 54);
    }

    public void setCurrentCamera(Camera cam) {
        this.currentCamera = cam;
    }

    public Camera getCurrentCamera() {
        return this.currentCamera;
    }

    public void reset() {
        pipeline.clear();
        entities.clear();
        currentCamera = null;
    }

    public BufferedImage getBuffer() {
        return this.buffer;
    }

    public void removeEntity(String entityName) {
        entities.remove(entityName);
    }

    public void drawDebugToWindow(Graphics2D g, Window window) {

        if (game.isDebugGreaterThan(0)) {
            drawDebugGrid(g, 32, scale);
            Collection<GameEntity> entities = game.getSceneManager().getActiveScene().getEntities().values();
            double scale = window.getFrame().getWidth() / buffer.getWidth();
            entities
                    .stream()
                    .filter(e -> Arrays.stream(debugFilter.split(",")).anyMatch(df -> e.getName().startsWith(df)))
                    .forEach(e -> {
                        moveToCameraViewport(g, getCurrentCamera(), scale, -1, !e.isStickToCamera());
                        if (plugins.containsKey(e.getClass())) {
                            RendererPlugin rp = ((RendererPlugin) plugins.get(e.getClass()));
                            rp.drawDebug(this, g, e, scale);
                        }
                        // if exists call behavior debugging draw operation.
                        //e.getBehaviors().stream().filter(b-> b.drawDebugInfo(game,g,e,scale));
                        moveToCameraViewport(g, getCurrentCamera(), scale, 1, !e.isStickToCamera());
                    });
            if (Optional.ofNullable(currentCamera).isPresent()) {
                drawCameraDebug(g, currentCamera, scale);
            }
        }

    }

    private void moveToCameraViewport(Graphics2D g, Camera currentCamera, double scale, double direction, boolean flag) {
        if (Optional.ofNullable(currentCamera).isPresent() && flag) {
            g.translate(direction * currentCamera.position.x * scale, direction * currentCamera.position.y * scale);
        }
    }

    public Font getDebugFont() {
        return debugFont;
    }
}
