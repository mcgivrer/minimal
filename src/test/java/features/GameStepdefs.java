package features;

import fr.snapgames.game.Game;
import io.cucumber.java8.En;

import fr.snapgames.game.Game.GameEntity;
import fr.snapgames.game.Game.World;
import fr.snapgames.game.Game.Camera;
import fr.snapgames.game.Game.Vector2D;


import java.awt.*;

public class GameStepdefs implements En {
    Game game;

    public GameStepdefs() {
        Then("I update {int} times the Game of {int} ms steps", (Integer nbUpdate, Integer step) -> {
            game = (Game) TestContext.get("game", new Game());
            World world = new World(
                    new Dimension(320,200),
                    new Vector2D(0,-0.981));
            game.setWorld(world);
            for (int i = 0; i < nbUpdate; i++) {
                game.update(step);
            }
        });
    }
}
