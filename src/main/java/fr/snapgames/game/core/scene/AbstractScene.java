package fr.snapgames.game.core.scene;

import java.util.ArrayList;
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
        this.entities.put(ge.name, ge);
        ge.getChild().forEach(c -> this.entities.put(c.name, c));
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
}
