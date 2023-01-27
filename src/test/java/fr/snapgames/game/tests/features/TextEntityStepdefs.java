package fr.snapgames.game.tests.features;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test steps for the {@link fr.snapgames.game.core.entity.TextEntity} class.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class TextEntityStepdefs implements En {
    Game game;

    public TextEntityStepdefs() {
        And("I add a TextEntity named {string} with text {string}", (String entityName, String textContent) -> {
            game = (Game) TestContext.get("game");
            TextEntity te = new TextEntity(entityName).setText(textContent);
            game.getSceneManager().getActiveScene().add(te);
        });
        Then("the TextEntity with name {string} exists in the current Scene", (String entityName) -> {
            game = (Game) TestContext.get("game");
            TextEntity te = (TextEntity) game.getSceneManager().getActiveScene().getEntity(entityName);
            Assertions.assertNotNull(te);
        });
        And("the TextEntity with name {string} has a text value {string}", (String entityName, String valueText) -> {
            TextEntity te = (TextEntity) game.getSceneManager().getActiveScene().getEntity(entityName);
            Assertions.assertEquals(valueText, te.text);
        });
    }

}
