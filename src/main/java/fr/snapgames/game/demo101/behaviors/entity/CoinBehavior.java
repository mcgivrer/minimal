package fr.snapgames.game.demo101.behaviors.entity;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.behaviors.CollisionResponseBehavior;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.CollisionEvent;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Optional;

public class CoinBehavior implements CollisionResponseBehavior<GameEntity> {
    @Override
    public void update(Game game, GameEntity entity, double dt) {
        // if player near this entity less than distance (attrDist),
        // a force (attrForce) is applied to entity to reach to player.
        GameEntity p = game.getSceneManager().getActiveScene().getEntity("player");
        if (Optional.ofNullable(p).isPresent()) {
            double attrDist = (double) entity.attributes.get("attractionDistance");
            double attrForce = (double) entity.attributes.get("attractionForce");
            entity.setCollisionBox(new Ellipse2D.Double(
                    entity.position.x - ((attrDist - entity.size.x) * 0.5),
                    entity.position.y - ((attrDist - entity.size.y) * 0.5),
                    attrDist,
                    attrDist));
            if (entity.getCollisionBox().getBounds2D().intersects(p.getBoundingBox().getBounds2D())) {
                Vector2D v = p.position.substract(entity.position);
                entity.forces.add(v.normalize().multiply(attrForce));
                entity.setAttribute("debugAttrColor", Color.RED);
            } else {
                entity.setAttribute("debugAttrColor", Color.YELLOW);
            }
        }
    }

    @Override
    public void draw(Game game, Graphics2D g, GameEntity e) {
        if (game.getDebug() > 1) {
            Stroke bckUp = g.getStroke();
            setDashLine(g);
            double attrDist = (double) e.getAttribute("attractionDistance", 0);
            if (attrDist > 0) {
                Color debugColor = (Color) e.getAttribute("debugAttrColor", Color.YELLOW);
                g.setColor(debugColor);
                g.draw(new Ellipse2D.Double(e.position.x - (attrDist - e.size.x) * 0.5, e.position.y - (attrDist - e.size.y) * 0.5, attrDist, attrDist));
                if (debugColor != Color.YELLOW) {
                    g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.6f));
                    g.fill(e.getBoundingBox());
                }
            }
            g.setStroke(bckUp);
        }
    }

    public void setDashLine(Graphics2D g) {
        float[] dash1 = {2f, 0f, 2f};

        BasicStroke bs1 = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                1.0f,
                dash1,
                2f);
        g.setStroke(bs1);
    }


    @Override
    public void collide(Game game, CollisionEvent ce) {
        GameEntity player = ce.getCollider();

        GameEntity coin = ce.getSource();
        if (player.getName().equals("player") && Optional.ofNullable(player).isPresent()) {
            if (coin.getBoundingBox().intersects(player.getBoundingBox().getBounds2D()) && coin.isActive()) {
                int score = (int) player.getAttribute("score", 0);
                score += (int) coin.getAttribute("value", 20);
                player.setAttribute("score", score);
                game.getSoundSystem().play("collectCoin", 1.0f);
                coin.markedAsDeleted(true);
                coin.setActive(false);
            }
        }
    }

    @Override
    public String getFilteredNames() {
        return "player";
    }
}
