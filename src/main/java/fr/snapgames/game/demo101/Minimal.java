package fr.snapgames.game.demo101;

import fr.snapgames.game.core.Game;

public class Minimal extends Game {


    public Minimal() {
        super();
    }

    /**
     * Entry point for executing game.
     *
     * @param args list of command line arguments
     */
    public static void main(String[] args) {

        Minimal game = new Minimal();
        game.run(args);
    }
}
