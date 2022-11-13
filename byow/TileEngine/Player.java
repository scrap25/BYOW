package byow.TileEngine;

public class Player {
    private int x;
    private int y;
    private TETile sittingOn;

    public Player(int randX, int randY) {
        x = randX;
        y = randY;
    }

    public void sittingOn(TETile teTile) {
        sittingOn = teTile;
    }
}
