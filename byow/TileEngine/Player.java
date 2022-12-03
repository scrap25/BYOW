package byow.TileEngine;

public class Player {
    private int x;
    private int y;
    private int keysCollected;
    private TETile sittingOn;
    private int visibileRange = 1;
    private int moves;

    public Player(int randX, int randY) {
        x = randX;
        y = randY;
    }

    public void setSittingOn(TETile teTile) {
        sittingOn = teTile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TETile getSittingOn() {
        return sittingOn;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getNumKeysCollected() {
        return keysCollected;
    }

    public void keyCollected() {
        keysCollected++;
    }

    public void madeMove() {
        moves++;
        if (moves % 10 == 0) {
            visibileRange++;
        }
    }

    public int getVisibleRange() {
        return visibileRange;
    }
}
