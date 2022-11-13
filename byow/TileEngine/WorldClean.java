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
                100,
                3, 6,
                Math.round(width/2), Math.round(height/2),
                0, width - 1,
                0, height - 1
        );
        Path path = generatePathOnWorld(pathParams, 50);
    }

    private Path generatePathOnWorld(PathParams pathParams, int delayMillis) {
        Path path = new Path(pathParams);
        world[pathParams.getStartX()][pathParams.getStartY()].makeFloor();
        path.add(world[pathParams.getStartX()][pathParams.getStartY()]);
        int maxNumSteps = pathParams.getMaxNumSteps();
        int minStepLength = pathParams.getMinStepLength();
        int maxStepLength = pathParams.getMaxStepLength();
        for (int i = 0; i < maxNumSteps; i++) {
            int randDirection = random.nextInt(4);
            int randStepLength = random.nextInt(minStepLength, maxStepLength);
            if (!isValidStep(path, randDirection, randStepLength)) {
                i--; // Ignore this attempt and try again
                continue;
            }
            takeStep(path, randDirection, randStepLength);
            if (delayMillis > 0) {
                drawAndPause(delayMillis);
            }
        }
        return path;
    }

    private boolean isValidStep(Path path, int direction, int stepLength) {
        WorldTile pathHead = path.getHead();
        int x = pathHead.getX();
        int y = pathHead.getY();
        return switch (direction) {
            case 0 -> // right
                    x + stepLength <= path.getParams().getMaxX();
            case 1 -> // down
                    y - stepLength >= path.getParams().getMinY();
            case 2 -> // left
                    x - stepLength >= path.getParams().getMinX();
            case 3 -> // up
                    y + stepLength <= path.getParams().getMaxY();
            default -> throw new RuntimeException("Invalid direction.");
        };
    }
    private void takeStep(Path path, int direction, int stepLength) {
        WorldTile pathHead = path.getHead();
        int x = pathHead.getX();
        int y = pathHead.getY();
        for (int i = 0; i < stepLength; i++) {
            switch (direction) {
                case 0: // right
                    x += 1;
                    break;
                case 1: // down
                    y -= 1;
                    break;
                case 2: // left
                    x -= 1;
                    break;
                case 3: // up
                    y += 1;
                    break;
                default:
                    throw new RuntimeException("Invalid direction.");
            }
            world[x][y].makeFloor();
            path.add(world[x][y]);
        }
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

    private void drawAndPause(int delayMillis) {
        ter.renderFrame(getAsTETiles());
        if (delayMillis > 0) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
