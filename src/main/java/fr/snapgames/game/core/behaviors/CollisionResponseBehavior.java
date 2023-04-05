package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.CollisionEvent;

/**
 * This interface propose implementation to respond to a {@link CollisionEvent}.
 *
 * @param <T> the colliding Entity object.
 * @author Frédéric Delorme
 * @since 0.0.7
 */
public interface CollisionResponseBehavior<T> extends Behavior<T> {
    /**
     * This {@link CollisionResponseBehavior#collide(Game, CollisionEvent)} method is called to process response for the given {@link CollisionEvent}.
     *
     * @param ce the {@link CollisionEvent} to be processed.
     */
    void collide(Game g, CollisionEvent ce);

    /**
     * Filter entities you want to test collision with based on a coma separated ist of name.
     * <p>Possible values : "player"  or "coin,energy,chest"</p>
     * <p>So all entity containing "player" for the first list wil be filtered in, all entities with name
     * containing "coin, chest of energy, will be filtered in.</p>
     *
     * @return names of the entity you want to test collision with.
     * @see fr.snapgames.game.core.entity.GameEntity
     */
    default String getFilteredNames() {
        return "";
    }
}
