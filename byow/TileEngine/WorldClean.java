package byow.TileEngine;

import java.util.ArrayList;
import java.util.Random;

public class WorldClean {
    private final Random random;

    private int width, height;
    private WorldTile[][] world;

    private ArrayList<Path> paths;
    private ArrayList<Room> rooms;

    // For seeing drawing slowly during development
    private TERenderer ter;

    public WorldClean(long seed, int width, int height, TERenderer ter) {
        random = new Random(seed);

        this.width = width;
        this.height = height;
        world = generateEmptyWorld(width, height);

        this.ter = ter;

        PathParams pathParams = new PathParams(
                5,
                3, 6,
                0, 0,
                0, width - 1,
                0, height - 1
        );
        Path path = generatePathOnWorld(pathParams);
    }

    private Path generatePathOnWorld(PathParams pathParams) {
        int maxNumSteps = pathParams.getMaxNumSteps();
        int minStepLength = pathParams.getMinStepLength();
        int maxStepLength = pathParams.getMaxStepLength();
        for (int i = 0; i < maxNumSteps; i++) {
            int randDirection = random.nextInt(4);
            int randStepLength = random.nextInt(minStepLength, maxStepLength);
            if (!isValidStep(randDirection, randStepLength)) {
                continue; // Ignore this step and try again
            }
            takeStep(randDirection, randStepLength);
        }
        return null; // TODO
    }

    private void takeStep(int randDirection, int randStepLength) {
        // TODO: Implement
    }

    private boolean isValidStep(int randDirection, int randStepLength) {
        // TODO: Implement
        return true;
    }

    // Creates and returns a new WorldTile[][] or empty tiles.
    private WorldTile[][] generateEmptyWorld(int width, int height) {
        WorldTile[][] emptyWorld = new WorldTile[width][height];
        for (int x = 0; x < emptyWorld.length; x++) {
            for (int y = 0; y < emptyWorld[x].length; y++) {
                emptyWorld[x][y] = new WorldTile(x, y, Tileset.NOTHING);
            }
        }
        return emptyWorld;
    }

    // Needed for displaying
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
