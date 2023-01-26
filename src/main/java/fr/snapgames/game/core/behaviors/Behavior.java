package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;

import java.awt.*;

/**
 * The Behavior interface to define specific processing on a GameEntity.
 */
public interface Behavior<T> {
    void update(Game game, T entity, double dt);

    void input(Game game, T entity);

    void draw(Game game, Graphics2D g, T entity);
}
