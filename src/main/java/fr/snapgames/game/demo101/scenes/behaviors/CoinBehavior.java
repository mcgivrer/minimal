package fr.snapgames.game.demo101.scenes.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CoinBehavior implements Behavior<GameEntity> {
    @Override
    public void input(Game g, GameEntity e) {

    }

    @Override
    public void draw(Game game, Graphics2D g, GameEntity e) {
        if (game.getDebug() > 1) {
            double attrDist = (double) e.getAttribute("attractionDistance", 0);
            if (attrDist > 0) {
                g.setColor(Color.YELLOW);
                Ellipse2D el = new Ellipse2D.Double(
                        e.position.x - (attrDist), e.position.y - (attrDist),
                        e.size.x + (attrDist * 2.0), e.size.y + (attrDist * 2.0));
                g.draw(el);
            }
        }
    }

    @Override
    public void update(Game game, GameEntity entity, double dt) {
        // if player near this entity less than distance (attrDist),
        // a force (attrForce) is applied to entity to reach to player.
        GameEntity p = game.getSceneManager().getActiveScene().getEntity("player");
        double attrDist = (double) entity.attributes.get("attractionDistance");
        double attrForce = (double) entity.attributes.get("attractionForce");

        if (p.position.add(entity.size.multiply(0.5)).distance(entity.position.add(p.size.multiply(0.5))) < attrDist) {
            Vector2D v = p.position.substract(entity.position);
            entity.forces.add(v.normalize().multiply(attrForce));
        }
        if (p.position.add(entity.size.multiply(0.5)).distance(entity.position.add(p.size.multiply(0.25))) < entity.size.add(p.size)
                .multiply(0.25).length()) {
            entity.setActive(false);
            int score = (int) p.getAttribute("score", 0);
            score += (int) p.getAttribute("value",20);
            p.setAttribute("score", score);
        }
    }

}
