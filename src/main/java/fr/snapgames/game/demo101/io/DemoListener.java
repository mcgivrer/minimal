package fr.snapgames.game.demo101.io;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.io.ActionListener;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.demo101.scenes.DemoScene;
import fr.snapgames.game.demo101.behaviors.CoinBehavior;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manage demo operation on entity to add or remove.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class DemoListener implements ActionListener {
    private final Game game;
    DemoScene scene;
    Configuration config;
    World world;

    public DemoListener(Game g, DemoScene scene) {
        this.game = g;
        this.scene = scene;
        config = g.getConfiguration();
        world = g.getPhysicEngine().getWorld();
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
            case KeyEvent.VK_B -> {
                GameEntity backGndImd = scene.getEntities().get("backgroundImage");
                if (Optional.ofNullable(backGndImd).isPresent()) {
                    if (backGndImd.isActive()) {
                        backGndImd.setActive(false);
                        scene.getEntities().values().stream().filter(e1 -> e1.name.startsWith("star_"))
                                .forEach(e2 -> e2.setActive(true));
                    } else {
                        backGndImd.setActive(true);
                        scene.getEntities().values().stream().filter(e1 -> e1.name.startsWith("star_"))
                                .forEach(e2 -> e2.setActive(false));
                    }
                }
            }
            case KeyEvent.VK_G -> {
                world.setGravity(world.getGravity().negate());
            }
            case KeyEvent.VK_R -> {
                scene.getEntities().get("rain").setActive(!scene.getEntities().get("rain").isActive());
            }

            case KeyEvent.VK_BACK_SPACE -> {
                game.getSceneManager().activate("title");
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
     * @param objectNameFilter the object name filter used to remove corresponding
     *                         {@link GameEntity}.
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
