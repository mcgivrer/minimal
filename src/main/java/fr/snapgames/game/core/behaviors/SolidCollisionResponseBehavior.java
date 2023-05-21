package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.CollisionEvent;
import fr.snapgames.game.core.math.Vector2D;

public class SolidCollisionResponseBehavior implements CollisionResponseBehavior<GameEntity> {
    @Override
    public void collide(Game g, CollisionEvent ce) {
        GameEntity platform = ce.getSource();
        GameEntity player = ce.getCollider();

        double xa = player.collisionBox.getBounds2D().getX();
        double xb = platform.collisionBox.getBounds2D().getX();
        double ya = player.collisionBox.getBounds2D().getY();
        double yb = platform.collisionBox.getBounds2D().getY();

        double xawa = player.collisionBox.getBounds2D().getX() + player.collisionBox.getBounds2D().getWidth();
        double xbwb = platform.collisionBox.getBounds2D().getX() + platform.collisionBox.getBounds2D().getWidth();
        double yaha = player.collisionBox.getBounds2D().getY() + player.collisionBox.getBounds2D().getHeight();
        double ybhb = platform.collisionBox.getBounds2D().getY() + platform.collisionBox.getBounds2D().getHeight();

        double dx = 0, dy = 0;
        if (xb > xa) {
            dx = xawa - xb;
        } else {
            dx = xbwb - xa;
        }
        if (yb > ya) {
            dy = yaha - yb;
        } else {
            dy = ybhb - ya;
        }

        Vector2D pv = new Vector2D(dx, dy);
        System.out.printf("%s vs %s : pv=(%f,%f)%n", player.getName(), platform.getName(), dx, dy);

        // test player bottom side
        if (platform.position.y < player.collisionBox.getBounds2D().getY() + player.collisionBox.getBounds2D().getHeight()) {
            player.position.y = platform.position.y - player.collisionBox.getBounds2D().getHeight();
            player.acceleration.y = 0;
            player.speed.y = 0;
            player.currentAnimation = "player_idle";
        } else
            // test player left side
            if (platform.position.y < player.position.y
                    && platform.position.x < player.collisionBox.getBounds2D().getY() + player.collisionBox.getBounds2D().getWidth()) {
                player.position.x = platform.position.x - player.collisionBox.getBounds2D().getWidth();
                player.acceleration.x = 0;
                player.speed.x = 0;
                player.currentAnimation = "player_idle";
            } else
                // test player right side
                if (platform.position.y < player.position.y
                        && platform.position.x + platform.size.x < player.collisionBox.getBounds2D().getY()) {
                    player.position.x = platform.position.x + platform.size.x;
                    player.updateBox();
                    player.acceleration.x = 0;
                    player.speed.x = 0;
                    player.currentAnimation = "player_idle";
                }

        player.updateBox();

    }

    @Override
    public String getFilteredNames() {
        return "player";
    }
}
