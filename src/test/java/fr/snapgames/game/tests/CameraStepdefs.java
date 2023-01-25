package fr.snapgames.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import fr.snapgames.game.Game;
import fr.snapgames.game.Game.Camera;
import fr.snapgames.game.Game.GameEntity;
import io.cucumber.java8.En;


public class CameraStepdefs implements En {
    private Game game;

    public CameraStepdefs() {
        And("no Camera is set", () -> {
            game = (Game) TestContext.get("game", new Game());
            game.getRenderer().setCurrentCamera(null);
        });
        And("I add a Camera named {string}", (String camName) -> {
            game = (Game) TestContext.get("game");
            Camera cam = new Camera(camName);
            game.getRenderer().setCurrentCamera(cam);
        });
        And("I add a Camera named {string} with tween at {double}", (String camName, Double tweenFactor) -> {
            game = (Game) TestContext.get("game");
            Camera cam = new Camera(camName).setTween(tweenFactor);
            game.getRenderer().setCurrentCamera(cam);
        });
        Then("the current Camera is not null", () -> {
            game = (Game) TestContext.get("game");
            assertNotNull(game.getRenderer().getCurrentCamera());
        });
        And("the current Camera name is {string}", (String camName) -> {
            game = (Game) TestContext.get("game");
            assertEquals(camName, game.getRenderer().getCurrentCamera().name);
        });
        And("set Camera {string} target as GameEntity {string}", (String camName, String targetName) -> {
            game = (Game) TestContext.get("game");
            GameEntity player = game.getEntities().get(targetName);
            game.getRenderer().getCurrentCamera().setTarget(player);
        });
        And("the current Camera {string} name is centered on {string}", (String camName, String targetName) -> {
            game = (Game) TestContext.get("game");
            Camera cam = game.getRenderer().getCurrentCamera();
            GameEntity target = game.getEntities().get(targetName);
            Game.Vector2D targetPos = target.position;
            Game.Vector2D camPos = cam.position;
            // define the area covered by the target
            Rectangle2D.Double r = new Rectangle2D.Double(targetPos.x, targetPos.y, target.size.x, target.size.y);
            // check is center of viewport area is in the target covered area.
            assertTrue(r.contains(
                    new Point2D.Double(
                            camPos.x + (cam.viewport.width * 0.5),
                            camPos.y + (cam.viewport.height * 0.5))));
        });
        And("set Camera {string} viewport as {int},{int}", (String camName, Integer vpWidth, Integer vpHeight) -> {
            game = (Game) TestContext.get("game");
            Camera cam = game.getRenderer().getCurrentCamera();
            cam.viewport = new Dimension(vpWidth, vpHeight);
        });


    }
}
