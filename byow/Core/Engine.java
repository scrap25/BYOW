package byow.Core;

import byow.TileEngine.GameEngine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Engine {

    private GameEngine gameEngine;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        gameEngine = new GameEngine();
        gameEngine.setupFrame();
        gameEngine.startGameLoop(null, true);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        boolean seedStarted = false;
        String seedString = "";
        boolean seedFinished = false;
        int i;
        for (i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!seedStarted && (c == 'n' || c == 'N')) {
                seedStarted = true;
            } else if (seedStarted && Character.isDigit(c)) {
                seedString += c;
            } else if (c == 's' || c == 'S') {
                seedFinished = true;
               break;
            }
        }
        if (seedFinished) {
            long seed = Long.parseLong(seedString);
            System.out.println("Using seed: " + seed);

            gameEngine = new GameEngine();
            gameEngine.startGameLoop(seed, false);
        }
        if (gameEngine == null) {
            return null;
        }
        return gameEngine.getWorldAsTETile();
    }

    @Override
    public String toString() {
        return gameEngine.getWorldAsString();
    }
}
