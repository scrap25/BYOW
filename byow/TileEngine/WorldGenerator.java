package byow.TileEngine;

import java.util.Random;

public class WorldGenerator {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        World world = new World(WIDTH, HEIGHT);
        TETile[][] teTiles = world.getAsTETiles();

        ter.renderFrame(teTiles);
    }


}
