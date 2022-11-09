package byow.TileEngine;

import java.util.Random;

public class World {

    private final Random random;
    private WorldTile[][] world;
    private int width, height;
    private Path path;

    private TERenderer ter;

    public World(long seed, int width, int height, TERenderer ter) {
        this.ter = ter;
        random = new Random(seed);
        this.width = width;
        this.height = height;
        world = generateEmptyWorld(width, height);
        path = generateMainPath();
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

    public TETile[][] getAsTETiles() {
        TETile[][] teTileWorld = new TETile[width][height];
        for (int x = 0; x < teTileWorld.length; x++) {
            for (int y = 0; y < teTileWorld[x].length; y++) {
                teTileWorld[x][y] = world[x][y].getTETile();
            }
        }
        return teTileWorld;
    }

    private Path generateMainPath() {
        int steps = 30;
        int delay = 500; // milliseconds
        Path newPath = new Path();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        System.out.println("Starting at (" + x + ", " + y + ")");
        world[x][y].placeAvatar();
        newPath.add(world[x][y]);
        int stepsTaken = 0;
        int lastDirectionIndex = -1;
        while (stepsTaken < steps) {
            int[] xyDirectionAndLength = getRandomDirectionAndLength(x, y, lastDirectionIndex % 2, 0.75);
            if (xyDirectionAndLength[3] == xyDirectionAndLength[4]) { // both 0
                continue;
            }
            lastDirectionIndex = xyDirectionAndLength[0];
            System.out.println(stepsTaken + ": Going direction " + lastDirectionIndex + " for " + Math.max(xyDirectionAndLength[3], xyDirectionAndLength[4]));
            for (int i = 0; i < xyDirectionAndLength[3]; i++) {
                x += xyDirectionAndLength[1];
                world[x][y].makeFloor();
                newPath.add(world[x][y]);
            }
            for (int j = 0; j < xyDirectionAndLength[4]; j++) {
                y += xyDirectionAndLength[2];
                world[x][y].makeFloor();
                newPath.add(world[x][y]);
            }
            ter.renderFrame(getAsTETiles());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stepsTaken++;
        }
        world[x][y].placeEndPath();
        return newPath;
    }

    private int[] getRandomDirectionAndLength(int x, int y, int forbiddenDirection, double factor) {
        int maxX = width;
        int maxY = height;
        //                   0:right   1:down   2:left    3:up
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
        int index = forbiddenDirection;
        while (index % 2 == forbiddenDirection) {
            index = random.nextInt(4);
        }
        int[] direction = directions[index];

        int xStepLength = 0;
        if (direction[0] == -1) {
            xStepLength = random.nextInt(Math.min(x + 1, maxX));
        } else if (direction[0] == 1) {
            xStepLength = random.nextInt(Math.max(maxX - x, 1));
        }
        xStepLength = (int) Math.round(xStepLength * factor);

        int yStepLength = 0;
        if (direction[1] == -1) {
            yStepLength = random.nextInt(Math.min(y + 1, maxY));
        } else if (direction[1] == 1) {
            yStepLength = random.nextInt(Math.max(maxY - y, 1));
        }
        yStepLength = (int) Math.round(yStepLength * factor);
        return new int[]{index, direction[0], direction[1], xStepLength, yStepLength};
    }
}
