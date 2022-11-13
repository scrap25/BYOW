package byow.TileEngine;

public class WorldGenerator {
    private static final long DEFAULT_SEED = 2873123;
    private static final int WIDTH = 44;
    private static final int HEIGHT = 44;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        WorldClean world = new WorldClean(DEFAULT_SEED, WIDTH, HEIGHT, ter);
        TETile[][] teTiles = world.getAsTETiles();

        ter.renderFrame(teTiles);
    }
}
