package fr.snapgames.game.core.math;

import fr.snapgames.game.core.entity.GameEntity;

public class CollisionEvent {
    private final GameEntity entitySource;
    private final GameEntity entityCollider;
    private final Vector2D penetration;

    public CollisionEvent(GameEntity source, GameEntity collider) {
        this.entitySource = source;
        this.entityCollider = collider;

        this.penetration = new Vector2D(
                entitySource.getCollisionBox().getBounds2D().getCenterX()
                        - collider.getCollisionBox().getBounds2D().getCenterX(),
                entitySource.getCollisionBox().getBounds2D().getCenterY()
                        - collider.getCollisionBox().getBounds2D().getCenterY()
        );
    }

    public GameEntity getSource() {
        return entitySource;
    }

    public GameEntity getCollider() {
        return entityCollider;
    }

    public Vector2D getPenetrationVector() {
        return penetration;
    }

}
