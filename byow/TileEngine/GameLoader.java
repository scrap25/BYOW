package byow.TileEngine;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class GameLoader {

    private static final String SAVE_FILE_NAME = "./savedGame.txt";

    private Long seed;
    private String keyHistory;

    public GameLoader() {
        seed = null;
        keyHistory = "";
    }


    // Returns true if there is already a game saved.
    public boolean loadGameFromFile() {
        In in = new In(SAVE_FILE_NAME);
        String stored = in.readLine();
        if (stored != null) {
            String[] parts = stored.split("#");
            seed = Long.parseLong(parts[0]);
            keyHistory = parts[1];
            return true;
        }
        return false;
    }

    public void clearSavedGame() {
        // TODO
    }

    public Long getSeed() {
        return seed;
    }

    public String getKeyHistory() {
        return keyHistory;
    }

    public void saveGame() {
        String gameAsString = seed + "#" + keyHistory;
        Out out = new Out(SAVE_FILE_NAME);
        out.print(gameAsString);
        out.close();
    }

    public void record(String keyChar) {
        keyHistory += keyChar;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }
}
