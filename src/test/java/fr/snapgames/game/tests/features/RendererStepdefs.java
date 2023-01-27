package fr.snapgames.game.tests.features;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;

/**
 * Test for {@link fr.snapgames.game.core.graphics.Renderer} service.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class RendererStepdefs implements En {
    Game game;

    public RendererStepdefs() {
        Then("I render the current Scene", () -> {
            game = (Game) TestContext.get("game");
            game.getRenderer().draw(new HashMap<>());
        });
        And("the {string} {string} has been rendered by the {string} plugin", (String entityType, String entityName, String pluginName) -> {
            game = (Game) TestContext.get("game");
            GameEntity ge = game.getSceneManager().getActiveScene().getEntity(entityName);
            String className = ge.getClass().getSimpleName();
            Assertions.assertEquals(entityType, className);
            Assertions.assertTrue(ge.getRenderedBy().getName().equals(pluginName));
        });
    }
}
