package byow.TileEngine;

import java.util.ArrayList;
import java.util.Random;

public class World {

    private final Random random;
    private WorldTile[][] world;
    private int width, height;
    private ArrayList<Path> paths;
    private ArrayList<Room> rooms;
    private TERenderer ter;

    public World(long seed, int width, int height, TERenderer ter) {
        this.ter = ter;
        random = new Random(seed);
        this.width = width;
        this.height = height;
        world = generateEmptyWorld(width, height);
        paths = generatePaths(0);
//        rooms = generateRooms(15);
    }

    private ArrayList<Path> generatePaths(int delay) {
        ArrayList<Path> newPaths = new ArrayList<>();
        int xfourth = Math.round(width/4);
        int yfourth = Math.round(height/4);
        int[][] startingPoints = {{xfourth, yfourth},{xfourth, 3*yfourth},{3*xfourth, yfourth},{3*xfourth, 3*yfourth}};
        for (int i = 0; i < startingPoints.length; i++) {
            int[] xy = startingPoints[i];
            newPaths.add(generatePath(xy[0], xy[1], delay));
        }
        return newPaths;
    }

    private ArrayList<Room> generateRooms(int numRooms)  {
        ArrayList<Room> newRooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            int randRoomWidth = random.nextInt(3, 6);
            int randRoomHeight = random.nextInt(3, 6);

            int randRoomX = random.nextInt(0, width - randRoomWidth);
            int randRoomY = random.nextInt(0, height - randRoomHeight);

            for (int x = randRoomX; x < randRoomX + randRoomWidth; x++) {
                for (int y = randRoomY; y < randRoomY + randRoomHeight; y++) {
                    world[x][y].makeRoom();
                }
            }
        }
        return newRooms;
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

    private Path generatePath(int startX, int startY, int delay) {
        int steps = 20;
        Path newPath = new Path();
        int x = startX;//Math.round(width / 2);//random.nextInt(width);
        int y = startY; //Math.round(height / 2);//random.nextInt(height);
        System.out.println("Starting at (" + x + ", " + y + ")");
        world[x][y].placeAvatar();
        newPath.add(world[x][y]);
        int stepsTaken = 0;
        int lastDirectionIndex = -1;
        while (stepsTaken < steps) {
            int[] xyDirectionAndLength = getRandomDirectionAndLength(x, y, lastDirectionIndex % 2, 0.75);
            if (xyDirectionAndLength == null) {
                continue;
            }
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
            lastDirectionIndex = xyDirectionAndLength[0];
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            stepsTaken++;
        }
        world[x][y].placeEndPath();
        return newPath;
    }

    /**
     *
     * @param x X position of path head
     * @param y Y position of path head
     * @param forbiddenDirection Either 0 (right, left) or 1 (down, up)
     * @param factor Factor to multiply final step length.
     * @return
     */
    private int[] getRandomDirectionAndLength(int x, int y, int forbiddenDirection, double factor) {
        int constantLength = 10;
        //                   0:right   1:down   2:left    3:up
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
        // {-1, 0} represents 1 unit in -x direction and 0 units in y
        int index = forbiddenDirection;
        while (index % 2 == forbiddenDirection) {
            // Choose a random direction until not one of the forbidden directions
            index = random.nextInt(4);
        }

        int[] direction = directions[index];

        int xLengthTillWall = 0;
        if (direction[0] == -1) {
            xLengthTillWall = x;
        } else if (direction[0] == 1) {
            xLengthTillWall = width - x - 1;
        }
        int xStepLength = random.nextInt(Math.min(4, xLengthTillWall), Math.min(xLengthTillWall, constantLength) + 1);//(int) Math.round(xStepLength * factor);

        int yLengthTillWall = 0;
        if (direction[1] == -1) {
            yLengthTillWall = y;
        } else if (direction[1] == 1) {
            yLengthTillWall = height - y - 1;
        }
        int yStepLength = random.nextInt(Math.min(4, yLengthTillWall), Math.min(yLengthTillWall, constantLength) + 1);//(int) Math.round(yStepLength * factor);
        if (xStepLength + yStepLength < 5) {
            return null;
        }
        return new int[]{index, direction[0], direction[1], xStepLength, yStepLength};
    }
}
