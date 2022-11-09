package byow.TileEngine;

public class World {

    private WorldTile[][] world;
    private int width, height;
    private Path path;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        world = generateEmptyWorld(width, height);
        path = generatePath();
    }

    private WorldTile[][] generateEmptyWorld(int width, int height) {
        WorldTile[][] emptyWorld = new WorldTile[width][height];
        for (int x = 0; x < emptyWorld.length; x++) {
            for (int y = 0; y < emptyWorld[x].length; y++) {
                emptyWorld[x][y] = new WorldTile(x, y, Tileset.NOTHING);
            }
        }
        return emptyWorld;
    }

    private Path generatePath() {
        // TODO
        return null;
    }

    public TETile[][] getAsTETiles() {
        TETile[][] teTileWorld = new TETile[width][height];
        for (int x = 0; x < teTileWorld.length; x++) {
            for (int y = 0; y < teTileWorld[x].length; y++) {
                teTileWorld[x][y] = world[x][y].getTETile();
            }
        }
        return teTileWorld;
    }
}
