package fr.snapgames.game.core.scene;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.Camera;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.math.PhysicEngine;
import fr.snapgames.game.core.scene.transition.Transition;

/**
 * The {@link AbstractScene} is implementing the basement for ane Scene,
 * providing all the internal mechanics for scene activation and switching;
 *
 * @author Frédéric Delorme
 * @since 0.0.7
 */
public abstract class AbstractScene implements Scene {

    protected Game game;
    protected String name;

    protected Configuration config;

    protected Transition transition;

    protected PhysicEngine pe;
    protected InputHandler inputHandler;
    protected Renderer renderer;

    protected Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    protected Map<String, Camera> cameras = new ConcurrentHashMap<>();
    protected Camera activeCamera;

    protected final Map<String, GameEntity> colliders = new ConcurrentHashMap<>();

    protected List<Behavior<Scene>> behaviors = new ArrayList<>();

    protected Map<String, Object> attributes = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void initialize(Game g) {
        config = g.getConfiguration();
        pe = g.getPhysicEngine();
        inputHandler = g.getInputHandler();
        renderer = g.getRenderer();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    protected AbstractScene(Game g, String name) {
        this.game = g;
        setName(name);
    }

    public void add(GameEntity ge) {
        this.entities.put(ge.getName(), ge);
        if (ge.isCollider()) {
            this.colliders.put(ge.getName(), ge);
        }
        ge.getChild().forEach(c -> this.entities.put(c.getName(), c));
    }

    @Override
    public void removeEntitiesMarkAsDeleted() {
        List<GameEntity> toBeDeleted = new ArrayList<>();
        entities.values().stream().filter(t -> t.isMarkedAsDelete()).forEach(t2 -> toBeDeleted.add(t2));
        // remove entity marked as deleted
        toBeDeleted.forEach(tbd -> {
            entities.remove(tbd.getName());
        });
    }

    /**
     * Add a new {@link Behavior} to the {@link Scene}.
     *
     * @param b the {@link Behavior} instance to be added to this {@link Scene}.
     * @return the updated Scene.
     */
    public Scene add(Behavior<Scene> b) {
        this.behaviors.add(b);
        return this;
    }

    /**
     * Retrieve a {@link GameEntity} from this {@link Scene} with its name.
     *
     * @param name the name of the {@link GameEntity} to be retrieved in the {@link Scene}.
     * @return instance of GameEntity corresponding to the name.
     */
    public GameEntity getEntity(String name) {
        return this.entities.get(name);
    }

    public Map<String, GameEntity> getEntities() {
        return this.entities;
    }

    @Override
    public void input(Game g, InputHandler ih) {

    }

    @Override
    public void update(Game g, double dt) {
        behaviors.stream().forEach(b -> {
            b.update(game, this, dt);
        });
        if (Optional.ofNullable(getActiveCamera()).isPresent()) {
            getActiveCamera().update(dt);
        }
    }

    @Override
    public void draw(int i, Game g, Renderer r) {
        // A default empty implementation.

    }

    /**
     * Retrieve the current existing {@link Transition} for this scene.
     * This {@link Transition} instance will be used to slightly transition from this scene (src) to another one (dst).
     *
     * @return the Transition implmentation if defined, else null.
     */
    @Override
    public Transition getTransition() {
        return this.transition;
    }


    /**
     * Retrieve a {@link Scene} attribute on its attrName, if not found, return the defaultValue.
     *
     * @param attrName     the name of the attribute to be retrieved.
     * @param defaultValue the default value returned if the attrName does not exist.
     * @param <T>          the T type of the value.
     * @return the value of the attribute.
     */
    @Override
    public <T> T getAttribute(String attrName, Object defaultValue) {
        return (T) attributes.getOrDefault(attrName, defaultValue);
    }

    /**
     * Set a new attribute on the scene.
     *
     * @param attrName  the name for this new attribute.
     * @param attrValue the value of the new attrName attribute.
     * @return the current updated Scene.
     */
    @Override
    public Scene setAttribute(String attrName, Object attrValue) {
        attributes.put(attrName, attrValue);
        return this;
    }

    /**
     * Retrieve all the possible colliding {@link GameEntity} map.
     *
     * @return a Map of GameEntity identified as possible colliding objects.
     */
    public Map<String, GameEntity> getColliders() {
        return this.colliders;
    }

    /**
     * Add a new {@link Camera} to this {@link Scene} and set this one the default
     * active one if it is the first one.
     *
     * @param cam the new Camera to add to the Scene.
     */
    public void add(Camera cam) {
        cameras.put(cam.getName(), cam);
        if (Optional.ofNullable(activeCamera).isEmpty()) {
            this.activeCamera = cam;
        }
    }

    /**
     * Set the new active {@link Camera} from the already existing ones.
     *
     * @param camName the name of the {@link Camera} to be activated.
     */
    public void setCurrentCamera(String camName) {
        this.activeCamera = cameras.get(camName);
    }

    /**
     * Retrieve the current active {@link Camera}.
     *
     * @return the active {@link Camera}.
     */
    @Override
    public Camera getActiveCamera() {
        return this.activeCamera;
    }
}
