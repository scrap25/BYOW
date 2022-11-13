package byow.TileEngine;

public class WorldHandler {
    private int width;
    private int height;
    private TERenderer ter;

    private World world;

    public WorldHandler(int width, int height) {
        this(width, height, -1);
    }

    public WorldHandler(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        this.ter = ter;
        if (seed != -1) {
            generateWorld(seed);
        }
    }

    private void generateWorld(long seed) {
        world = new World(seed, this.width, this.height, this.ter);
    }
    public void renderWorld() {
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
}
