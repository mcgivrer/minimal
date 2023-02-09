package fr.snapgames.game.core.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.config.Configuration;
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

    @Override
    public String getName() {
        return "default";
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
        this.name = name;
    }

    public void add(GameEntity ge) {
        game.getRenderer().addEntity(ge);
        game.getPhysicEngine().addEntity(ge);
        this.entities.put(ge.name, ge);
        ge.getChild().forEach(c -> this.entities.put(c.name, c));
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

    }
}
