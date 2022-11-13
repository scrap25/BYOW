package byow.TileEngine;

public class Player {
    private int x;
    private int y;
    private TETile sittingOn;

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
}
