package features;

import fr.snapgames.game.Game;
import fr.snapgames.game.Game.GameEntity;
import io.cucumber.java8.En;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameEntityStepdefs implements En {

    Game game;

    public GameEntityStepdefs() {
        Given("a Game is instantiated", () -> {
            game = (Game) TestContext.get("game", new Game("/test.properties", true));
        });
        And("the entities map is empty", () -> {
            assertTrue(game.getEntities().isEmpty());
        });
        Then("I Add a new GameEntity named {string}", (String entityName) -> {
            game = (Game) TestContext.get("game");
            game.add(new Game.GameEntity(entityName));
        });
        And("the entities map size is {int}", (Integer nbEntities) -> {
            assertEquals(nbEntities.intValue(), game.getEntities().size());
        });
        And("I Add a new GameEntity named {string} at {double},{double}", (String entityName, Double posX, Double posY) -> {
            game = (Game) TestContext.get("game");
            GameEntity e = new GameEntity(entityName)
                    .setPosition(new Game.Vector2D(posX, posY));
            game.add(e);
        });
        And("the GameEntity {string} is stick to camera", (String entityName) -> {
            game = (Game) TestContext.get("game");
            GameEntity e = game.getEntities().get(entityName);
            e.stickToCamera(true);
        });
    }

}
