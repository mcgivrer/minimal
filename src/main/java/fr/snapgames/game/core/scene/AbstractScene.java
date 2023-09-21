package fr.snapgames.game.core.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.math.PhysicEngine;

public abstract class AbstractScene implements Scene {

    Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    protected Game game;
    protected String name;

    protected Configuration config;
    protected PhysicEngine pe;
    protected InputHandler inputHandler;
    protected Renderer renderer;

    protected List<Behavior<Scene>> behaviors = new ArrayList<>();

    public Map<String, Object> attributes = new HashMap<>();

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
        game.getRenderer().addEntity(ge);
        game.getPhysicEngine().addEntity(ge);
        this.entities.put(ge.getName(), ge);
        ge.getChild().forEach(c -> this.entities.put(c.getName(), c));
    }

    @Override
    public void removeEntitiesMarkAsDeleted() {
        List<GameEntity> toBeDeleted = new ArrayList<>();
        entities.values().stream().filter(t -> t.isMarkedAsDelete()).forEach(t2 -> toBeDeleted.add(t2));
        // remove entity marked as deleted
        toBeDeleted.forEach(tbd -> {
            entities.remove(tbd.getName());
            game.getRenderer().removeEntity(tbd.getName());
            game.getPhysicEngine().removeEntity(tbd.getName());
        });
    }

    /**
     * Add a behavior to the scene.
     *
     * @param b
     * @return
     */
    public Scene add(Behavior<Scene> b) {
        this.behaviors.add(b);
        return this;
    }

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
    }

    @Override
    public void draw(Game g, Renderer r) {
        // A default empty implementation.

    }


    public <T> T getAttribute(String attrName, Object defaultValue) {
        return (T) attributes.getOrDefault(attrName, defaultValue);
    }


    public Scene setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }
}
