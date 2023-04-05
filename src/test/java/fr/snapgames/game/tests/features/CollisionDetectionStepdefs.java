package fr.snapgames.game.tests.features;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.tests.features.scenes.TestScene;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

public class CollisionDetectionStepdefs implements En {
    Game game;

    public CollisionDetectionStepdefs() {
        Given("a Game is instantiated with configuration {string}", (String configurationFileName) -> {
            game = (Game) TestContext.get("game", new Game(configurationFileName, true));
            game.initialize(new String[]{"testCounter=1"});

        });
        And("A Scene {string} is created", (String sceneName) -> {
            game = (Game) TestContext.get("game");
            game.getSceneManager().add(new TestScene(game, sceneName));
        });
        And("A GameEntity {string} with size of {double}x{double} is created at {double},{double} in Scene {string}",
                (String entityName, Double width, Double height, Double x, Double y, String sceneName) -> {
                    game = (Game) TestContext.get("game");
                    Scene scene = game.getSceneManager().getScene(sceneName);
                    GameEntity entity = new GameEntity(entityName)
                            .setPosition(new Vector2D(x, y))
                            .setSize(new Vector2D(width, height))
                            .setColliderFlag(true)
                            .setType(EntityType.RECTANGLE);
                    scene.add(entity);
                });
        Then("A CollisionEvent happened between {string} and {string}", (String ent1Name, String ent2Name) -> {
            game = (Game) TestContext.get("game");
            game.loop();
            Assertions.assertEquals(2, game.getPhysicEngine().getCollisionEvents().size());
            System.out.printf("Collision: %s vs. %s%n",
                    game.getPhysicEngine().getCollisionEvents().get(0).getSource().getName(),
                    game.getPhysicEngine().getCollisionEvents().get(0).getCollider().getName());
            System.out.printf("Collision: %s vs. %s%n",
                    game.getPhysicEngine().getCollisionEvents().get(1).getSource().getName(),
                    game.getPhysicEngine().getCollisionEvents().get(1).getCollider().getName());
        });
    }
}
