package fr.snapgames.game.tests.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;
import io.cucumber.java8.En;


public class GameEntityStepdefs implements En {

    Game game;

    public GameEntityStepdefs() {
        Given("a Game is instantiated", () -> {
            TestContext.clear();
            Game g = new Game("/test.properties", true);
            g.initialize(new String[]{});
            game = (Game) TestContext.get("game", g);
            game.getSceneManager().activateDefaultScene();
        });
        And("the entities map is empty",
                () -> assertTrue(game.getSceneManager().getActiveScene().getEntities().isEmpty()));
        Then("I Add a new GameEntity named {string}", (String entityName) -> {
            game = (Game) TestContext.get("game");
            game.getSceneManager().getActiveScene().add(new GameEntity(entityName));
        });
        And("the entities map size is {int}",
                (Integer nbEntities) -> assertEquals(nbEntities.intValue(), game.getSceneManager().getActiveScene().getEntities().size()));
        And("I add a new GameEntity named {string} at {double},{double}", (String entityName, Double posX, Double posY) -> {
            game = (Game) TestContext.get("game");
            GameEntity e = new GameEntity(entityName)
                    .setPosition(new Vector2D(posX, posY));
            game.getSceneManager().getActiveScene().add(e);
        });
        And("the GameEntity {string} is stick to camera", (String entityName) -> {
            game = (Game) TestContext.get("game");
            GameEntity e = game.getSceneManager().getActiveScene().getEntities().get(entityName);
            e.setStickToCamera(true);
        });
    }

}
