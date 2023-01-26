package fr.snapgames.game.tests;

import java.awt.Dimension;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import io.cucumber.java8.En;

public class GameStepdefs implements En {
    Game game;

    public GameStepdefs() {
        Then("I update {int} times the Game of {int} ms steps", (Integer nbUpdate, Integer step) -> {
            game = (Game) TestContext.get("game", new Game());
            World world = new World(
                    new Dimension(320,200),
                    new Vector2D(0,-0.981));
            game.getPhysicEngine().setWorld(world);
            for (int i = 0; i < nbUpdate; i++) {
                game.update(step);
            }
        });
    }
}
