package byow.TileEngine;

public class WorldGenerator {
    private static final long DEFAULT_SEED = 2873123;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 44;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        for (int seed = 0; seed < 5000; seed++) {
            WorldClean world = new WorldClean(seed, WIDTH, HEIGHT, ter);
            TETile[][] teTiles = world.getAsTETiles();

            ter.renderFrame(teTiles);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
