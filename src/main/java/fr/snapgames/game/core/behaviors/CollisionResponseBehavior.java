package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.CollisionEvent;

/**
 * The {@link CollisionResponseBehavior} interface propose implementation to respond to a {@link CollisionEvent}.
 *
 * <p>This can be used as in the following sample code:</p>
 * <pre>
 * public class CoinBehavior implements CollisionResponseBehavior<GameEntity> {
 *   public void collide(Game game, CollisionEvent ce) {
 *     GameEntity player = ce.getCollider();
 *
 *     GameEntity coin = ce.getSource();
 *     if (player.getName().equals("player") && Optional.ofNullable(player).isPresent()) {
 *       if (coin.getBoundingBox().intersects(player.getBoundingBox().getBounds2D()) && coin.isActive()) {
 *          int score = (int) player.getAttribute("score", 0);
 *          score += (int) coin.getAttribute("value", 20);
 *          player.setAttribute("score", score);
 *          game.getSoundSystem().play("collectCoin", 1.0f);
 *          coin.markedAsDeleted(true);
 *       }
 *     }
 *   }
 *   public String getFilteredNames() {
 *     return "player";
 *   }
 * }
 * </pre>
 *
 * <p>This sample code illustrate the Collision response between a {@link fr.snapgames.game.core.entity.GameEntity} named "coin"
 * and another {@link fr.snapgames.game.core.entity.GameEntity} named "player" collecting coins.</p>
 * <p>When "coin" collides with "player"n, the "value" attribute of "coin" is added to the "score" attributes of "payer",
 * and the "coins" {@link fr.snapgames.game.core.entity.GameEntity} is marked as to be deleted.</p>
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
