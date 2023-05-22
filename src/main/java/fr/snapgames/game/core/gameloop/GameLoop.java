package fr.snapgames.game.core.gameloop;

import java.util.Map;

public interface GameLoop {

    void loop(Map<String, Object> context);

    boolean isExit();

    boolean isTestMode();
}
