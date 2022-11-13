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

    public void makeFloor() {
        tile = Tileset.FLOOR;
    }

    public void placeAvatar() {
        tile = Tileset.AVATAR;
    }

    public void placeEndPath() {
        tile = Tileset.FLOWER;
    }

    public void makeRoom() {
        tile = Tileset.ROOM;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFloor() {
        return tile.equals(Tileset.FLOOR);
    }

    public boolean isRoom() {
        return tile.equals(Tileset.ROOM);
    }

    public boolean isWall() {
        return tile.equals(Tileset.WALL);
    }

    public boolean isEmpty() {
        return tile.equals(Tileset.NOTHING);
    }

    public void makeWall() {
        tile = Tileset.WALL;
    }

    public void makeFlower() {
        tile = Tileset.FLOWER;
    }
}
