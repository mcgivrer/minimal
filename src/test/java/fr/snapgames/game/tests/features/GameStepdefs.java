package fr.snapgames.game.tests.features;

import java.awt.Dimension;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import fr.snapgames.game.core.scene.Scene;
import io.cucumber.java8.En;

public class GameStepdefs implements En {
    Game game;

    public GameStepdefs() {
        Then("I update {int} times the Game of {int} ms steps", (Integer nbUpdate, Integer step) -> {
            game = (Game) TestContext.get("game");
            World world = new World(
                    new Dimension(320, 200),
                    new Vector2D(0, -0.981));
            game.getPhysicEngine().setWorld(world);
            for (int i = 0; i < nbUpdate; i++) {
                game.update(step);
                Scene scene = game.getSceneManager().getActiveScene();
                Renderer rdr = game.getRenderer();
                System.out.printf("camera %s position: %f,%f%n",
                        rdr.getCurrentCamera().name,
                        rdr.getCurrentCamera().position.x,
                        rdr.getCurrentCamera().position.y);

                scene.getEntities().values().forEach(e -> {
                    System.out.printf("entity %s position: %f,%f%n",
                            e.name,
                            e.position.x,
                            e.position.y);
                });
            }
        });
    }
}
