package fr.snapgames.game.core.gameloop;

import fr.snapgames.game.core.Game;

import java.util.Map;

public interface GameLoop {

    void loop(Map<String, Object> context);

    boolean isExit();
}
