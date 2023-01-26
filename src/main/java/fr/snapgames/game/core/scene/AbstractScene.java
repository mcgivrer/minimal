package fr.snapgames.game.core.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;

public abstract class AbstractScene implements Scene {

    Map<String, GameEntity> entities = new ConcurrentHashMap<>();
    Game game;

    protected AbstractScene(Game g) {
        game = g;
    }

    protected void add(GameEntity ge) {
        game.getRenderer().addEntity(ge);
        game.getPhysicEngine().addEntity(ge);
    }

    @Override
    public void input(Game g, InputHandler ih) {

    }

    @Override
    public void update(Game g, double dt) {

    }
}
