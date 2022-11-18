package byow.TileEngine;

public class WorldHandler {
    private int width;
    private int height;
    private TERenderer ter;
    private boolean terInitialized;
    private World world;

    public WorldHandler(int width, int height) {
        this(width, height, -1, true);
    }

    public WorldHandler(int width, int height, long seed, boolean render) {
        this.width = width;
        this.height = height;
        this.ter = new TERenderer();
        this.terInitialized = false;
        if (seed != -1) {
            generateWorld(seed);
        }
    }

    private void generateWorld(long seed) {
        world = new World(seed, this.width, this.height, this.ter);
    }
    public void renderWorld() {
        if (!terInitialized) {
            ter.initialize(width, height);
            terInitialized = true;
        }
        TETile[][] teTiles = world.getAsTETiles();
        ter.renderFrame(teTiles);
    }

    public void addPlayer() {
        world.addPlayer();
    }
    public static void main(String[] args) {
        WorldHandler worldHandler = new WorldHandler(80, 44);

        for (int seed = 0; seed < 5000; seed++) {
            worldHandler.generateWorld(seed);
            worldHandler.renderWorld();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void movePlayer(String direction) {
        world.movePlayer(direction);
    }

    public TETile[][] getWorldAsTETile() {
        return world.getAsTETiles();
    }

    public String toString() {
        String worldAsString = "";
        TETile[][] worldTiles = getWorldAsTETile();
        for (int x = 0; x < worldTiles.length; x++) {
            for (int y = worldTiles[x].length - 1; y >= 0; y--) {
                char c = worldTiles[x][y].character();
                if (c == ' ') {
                    c = '0';
                }
                worldAsString += c;
            }
        }
        return worldAsString;
    }

    public String getTileDescAt(int x, int y) {
        return world.getTileDescAt(x, y);
    }
}
