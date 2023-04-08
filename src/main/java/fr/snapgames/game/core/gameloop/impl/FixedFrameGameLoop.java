package fr.snapgames.game.core.gameloop.impl;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.gameloop.GameLoop;

import java.util.HashMap;
import java.util.Map;

public class FixedFrameGameLoop implements GameLoop {

    private final Game game;
    double FPS = 60.0;
    double fpsDelay = 1000000.0 / 60.0;

    public FixedFrameGameLoop(Game game) {
        this.game = game;
    }

    @Override
    public void loop(Map<String, Object> context) {
// elapsed Game Time
        double start = 0;
        double end = 0;
        double dt = 0;
        // FPS measure
        long frames = 0;
        long realFPS = 0;

        long ups = 0;
        long realUPS = 0;
        long timeFrame = 0;
        long loopCounter = 0;
        int maxLoopCounter = (int) game.getConfiguration().get(ConfigAttribute.EXIT_TEST_COUNT_FRAME);
        Map<String, Object> loopData = new HashMap<>();
        while (!isExit() && !isTestMode()
                && !(maxLoopCounter != -1 && loopCounter > maxLoopCounter)) {
            start = System.nanoTime() / 1000000.0;
            loopCounter++;
            game.input();
            if (!game.isUpdatePause()) {
                game.update(dt * .04);
                ups += 1;
            }

            frames += 1;
            timeFrame += dt;
            if (timeFrame > 1000) {
                realFPS = frames;
                frames = 0;
                realUPS = ups;
                ups = 0;
                timeFrame = 0;
            }
            loopData.put("cnt", loopCounter);
            loopData.put("fps", realFPS);
            loopData.put("ups", realUPS);

            loopData.put("pause", game.isUpdatePause() ? "ON" : "OFF");
            loopData.put("obj", game.getSceneManager().getActiveScene().getEntities().size());
            loopData.put("scn", game.getSceneManager().getActiveScene().getName());
            loopData.put("dbg", game.getDebug());

            game.draw(loopData);
            waitUntilStepEnd(dt);

            end = System.nanoTime() / 1000000.0;
            dt = end - start;
        }

    }

    private boolean isTestMode() {
        return game.isTestMode();
    }

    private void waitUntilStepEnd(double dt) {
        if (dt < fpsDelay) {
            try {
                Thread.sleep((long) (fpsDelay - dt) / 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.printf("ERROR: Unable to wait for %d ms: %s%n", fpsDelay - dt, e.getMessage());
            }
        }
    }

    @Override
    public boolean isExit() {
        return game.isExitRequested();
    }
}