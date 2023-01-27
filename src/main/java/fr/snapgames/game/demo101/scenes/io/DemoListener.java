package fr.snapgames.game.demo101.scenes.io;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.config.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.demo101.scenes.DemoScene;
import fr.snapgames.game.demo101.scenes.behaviors.CoinBehavior;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage demo operation on entity to add or remove.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class DemoListener implements KeyListener {
    DemoScene scene;
    Configuration config;
    World world;

    public DemoListener(Game g, DemoScene scene) {
        this.scene = scene;
        config = g.getConfiguration();
        world = g.getPhysicEngine().getWorld();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP -> {
                addNewBalls("ball_", 10);
            }
            case KeyEvent.VK_PAGE_DOWN -> {
                removeAllObjectByNameFilter("ball_");
            }
            case KeyEvent.VK_DELETE -> {
                removeNbObjectByNameFilter("ball_", 10);
            }
        }
    }

    /**
     * Add new Balls in the view
     *
     * @param objectName base name for the new balls
     * @param nb         the number of balls to create.
     */
    private void addNewBalls(String objectName, int nb) {
        scene.createCoins(objectName, nb, world, new CoinBehavior());
    }

    /**
     * Remove a number of {@link GameEntity} based on a filtering name.
     *
     * @param objectName the name to filter entities on.
     * @param nb         the number of object to be removed.
     */
    private void removeNbObjectByNameFilter(String objectName, int nb) {
        List<GameEntity> toBeRemoved = new ArrayList<>();
        int count = 0;
        for (GameEntity e : scene.getEntities().values()) {
            if (e.name.contains(objectName)) {
                toBeRemoved.add(e);
                count++;
                if (count > nb) {
                    break;
                }
            }
        }
        toBeRemoved.forEach(e -> scene.getEntities().remove(e.name));
    }

    /**
     * Remove all {@link GameEntity} based on a filtering name.
     *
     * @param objectNameFilter the object name filter used to remove corresponding {@link GameEntity}.
     */
    private void removeAllObjectByNameFilter(String objectNameFilter) {
        List<GameEntity> toBeRemoved = new ArrayList<>();
        for (GameEntity e : scene.getEntities().values()) {
            if (e.name.contains(objectNameFilter)) {
                toBeRemoved.add(e);
            }
        }
        toBeRemoved.forEach(e -> scene.getEntities().remove(e.name));
    }
}
