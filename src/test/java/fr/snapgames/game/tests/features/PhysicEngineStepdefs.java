package fr.snapgames.game.tests.features;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;
import io.cucumber.java8.En;

import java.awt.*;

/**
 * Step tests for {@link fr.snapgames.game.core.math.PhysicEngine}
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class PhysicEngineStepdefs implements En {
    private Game game;

    public PhysicEngineStepdefs() {
        And("I Add a World with a play area of {int} x {int}", (Integer playAreaWidth, Integer playAreaHeight) -> {
            game = (Game) TestContext.get("game");
            game.getPhysicEngine().setWorld(
                    new World(
                            new Dimension(playAreaWidth, playAreaHeight),
                            new Vector2D(0, 0)));
        });
    }
}
