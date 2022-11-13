package byow.TileEngine;

import java.util.ArrayList;
import java.util.Random;

public class WorldClean {
    private final int RIGHT = 0;
    private final int DOWN = 1;
    private final int LEFT = 2;
    private final int UP = 3;
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

        // Make 4 paths each starting from center going toward the 4 quadrants
        int stepsPerQuad = 40;
        PathParams quad1 = new PathParams(
                stepsPerQuad,
                3, 7,
                Math.round(width/2), Math.round(height/2),
                Math.round(width/2), width - 1,
                Math.round(height/2), height - 1
        );
        PathParams quad2 = new PathParams(
                stepsPerQuad,
                3, 7,
                Math.round(width/2), Math.round(height/2),
                0, Math.round(width/2),
                Math.round(height/2), height - 1
        );
        PathParams quad3 = new PathParams(
                stepsPerQuad,
                3, 7,
                Math.round(width/2), Math.round(height/2),
                0, Math.round(width/2),
                0, Math.round(height/2)
        );
        PathParams quad4 = new PathParams(
                stepsPerQuad,
                3, 7,
                Math.round(width/2), Math.round(height/2),
                Math.round(width/2), width - 1,
                0, Math.round(height/2)
        );
        int delay = 50;
        Path path1 = generatePathOnWorld(quad1, delay);
        Path path2 = generatePathOnWorld(quad2, delay);
        Path path3 = generatePathOnWorld(quad3, delay);
        Path path4 = generatePathOnWorld(quad4, delay);
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
            if (!stepInBounds(path, randDirection, randStepLength)) {
                i--; // Ignore this attempt and try again
//                System.out.println("Out of bounds step of length " + randStepLength + " in direction " + randDirection);
                continue;
            }
            if (!validStep(path, randDirection, randStepLength)) {
                i--; // Ignore this attempt and try again
//                System.out.println("Invalid step of length " + randStepLength + " in direction " + randDirection);
                continue;
            }
            takeStep(path, randDirection, randStepLength);
            if (delayMillis > 0) {
                drawAndPause(delayMillis);
            }
        }
        return path;
    }

    private boolean validStep(Path path, int direction, int stepLength) {
        WorldTile pathHead = path.getHead();
        int x = pathHead.getX();
        int y = pathHead.getY();
        int adjInARow = 0;
        for (int i = 0; i < stepLength; i++) {
            switch (direction) {
                case RIGHT:
                    x += 1;
                    break;
                case DOWN:
                    y -= 1;
                    break;
                case LEFT:
                    x -= 1;
                    break;
                case UP:
                    y += 1;
                    break;
                default:
                    throw new RuntimeException("Invalid direction.");
            }
            if (hasTwoAdjFloorNeighbors(x, y, direction)) {
                adjInARow++;
                if (adjInARow == 2) {
                    return false;
                }
            } else {
                adjInARow = 0;
            }
        }
        return true;
    }

    private boolean hasTwoAdjFloorNeighbors(int midX, int midY, int direction) {
        int[][] twoAdjacentNeighbors = {{-2, -1}, {-1, 1}, {1, 2}};
        for (int i = 0; i < twoAdjacentNeighbors.length; i++) {
            int[] n = twoAdjacentNeighbors[i];
            if (direction % 2 == 0) { // 0: right, 2: left
                try { // TODO: This try catch is lazy way of dealing with the edge cases
                    if (world[midX][midY + n[0]].isFloor() && world[midX][midY + n[1]].isFloor()) {
                        return true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            } else if (direction % 2 == 1) { // 1: down, 3: up
                try {
                    if (world[midX + n[0]][midY].isFloor() && world[midX + n[1]][midY].isFloor()) {
                        return true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
        return false;
    }

    private boolean stepInBounds(Path path, int direction, int stepLength) {
        WorldTile pathHead = path.getHead();
        int x = pathHead.getX();
        int y = pathHead.getY();
        return switch (direction) {
            case RIGHT ->
                    x + stepLength <= path.getParams().getMaxX();
            case DOWN ->
                    y - stepLength >= path.getParams().getMinY();
            case LEFT ->
                    x - stepLength >= path.getParams().getMinX();
            case UP ->
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
                case RIGHT:
                    x += 1;
                    break;
                case DOWN:
                    y -= 1;
                    break;
                case LEFT:
                    x -= 1;
                    break;
                case UP:
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
