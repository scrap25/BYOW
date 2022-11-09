package byow.TileEngine;

public class WorldTile {

    private int x, y;
    private TETile tile;

    public WorldTile(int x, int y, TETile tile) {
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    public TETile getTETile() {
        return tile;
    }
}
