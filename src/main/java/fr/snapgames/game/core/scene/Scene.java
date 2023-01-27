package fr.snapgames.game.core.scene;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;

import java.util.Map;

public interface Scene {

    String getName();

    void setName(String name);

    void initialize(Game g);

    void loadResources(Game g);

    void create(Game g);

    void input(Game g, InputHandler ih);

    void update(Game g, double dt);

    void draw(Game g, Renderer r);

    void dispose(Game g);

    Map<String, GameEntity> getEntities();

    GameEntity getEntity(String name);

    void add(GameEntity e);
}
