package fr.snapgames.game.tests.features;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.graphics.Animations;
import fr.snapgames.game.core.scene.Scene;
import fr.snapgames.game.tests.features.scenes.TestScene;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

public class AnimationsStepdefs implements En {
    Game game;
    Animations animations;

    public AnimationsStepdefs() {
        And("An Animations is loaded with {string} from scene {string}", (String animFileName, String sceneName) -> {
            game = (Game) TestContext.get("game");
            Scene scene = game.getSceneManager().getActiveScene();
            animations = (Animations) TestContext.get("animations", new Animations(animFileName));
        });
        And("The GameEntity {string} is set with currentAnimation to {string}", (String gameEntityName, String animName) -> {
            game = (Game) TestContext.get("game");
            Scene scene = game.getSceneManager().getActiveScene();
            GameEntity player = scene.getEntity(gameEntityName);
            Animations animations = (Animations) TestContext.get("animations");
            player.add(animName, animations.get(animName));
            player.setAnimation(animName);
        });
        Then("The GameEntity {string} has animation of {int} frames", (String gameEntityName, Integer nbAnimationFrames) -> {
            game = (Game) TestContext.get("game");
            Scene scene = game.getSceneManager().getActiveScene();
            GameEntity player = scene.getEntity(gameEntityName);
            Assertions.assertEquals(nbAnimationFrames, player.getAnimations().get(player.currentAnimation).getFrames().length);
        });
    }
}
