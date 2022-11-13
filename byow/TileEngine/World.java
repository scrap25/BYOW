package byow.TileEngine;

import java.util.ArrayList;
import java.util.Random;

public class World {
    private final int RIGHT = 0;
    private final int DOWN = 1;
    private final int LEFT = 2;
    private final int UP = 3;
    private final Random random;

    private int width, height;
    private WorldTile[][] world;

    private ArrayList<Path> paths;
    private ArrayList<Room> rooms;

    private Player player;
    // For seeing drawing slowly during development
    private TERenderer ter;

    public World(long seed, int width, int height, TERenderer ter) {
        random = new Random(seed);

        this.width = width;
        this.height = height;
        world = generateEmptyWorld(width, height);

        this.ter = ter;

        createPathsAndRooms();
        addWalls();
    }

    private void addWalls() {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[x].length; y++) {
                WorldTile tile = world[x][y];
                if (!tile.isEmpty() && !tile.isWall()) {
                    convertNeighborsToWall(x, y);
                }
            }
        }
    }

    private void convertNeighborsToWall(int midX, int midY) {
        for (int x = midX - 1; x <= midX + 1; x++) {
            for (int y = midY - 1; y <= midY + 1; y++) {
                if (x == midX && y == midY) {
                    continue;
                }
                try {
                    if (world[x][y].isEmpty()) {
                        world[x][y].makeWall();
                    }
                } catch (IndexOutOfBoundsException e) {
                    // TODO: Fix this lazy way of handling edge cases.
                }
            }
        }
    }

    private void createPathsAndRooms() {
        // Make 4 paths each starting from center going toward the 4 quadrants
        int stepsPerQuad = 50;
        int minStepLength = 3;
        int maxStepLength = 7;
        PathParams quad1 = new PathParams(
                stepsPerQuad,
                minStepLength, maxStepLength,
                Math.round(width/2), Math.round(height/2),
                Math.round(width/2), width - 2,
                Math.round(height/2), height - 2
        );
        PathParams quad2 = new PathParams(
                stepsPerQuad,
                minStepLength, maxStepLength,
                Math.round(width/2), Math.round(height/2),
                1, Math.round(width/2),
                Math.round(height/2), height - 2
        );
        PathParams quad3 = new PathParams(
                stepsPerQuad,
                minStepLength, maxStepLength,
                Math.round(width/2), Math.round(height/2),
                1, Math.round(width/2),
                1, Math.round(height/2)
        );
        PathParams quad4 = new PathParams(
                stepsPerQuad,
                minStepLength, maxStepLength,
                Math.round(width/2), Math.round(height/2),
                Math.round(width/2), width - 2,
                1, Math.round(height/2)
        );
        int delay = 0;
        PathParams[] quads = {quad1, quad2, quad3, quad4};
        for (int i = 0; i < quads.length; i++) {
            PathParams quad = quads[i];
            Path path = generatePathOnWorld(quad, delay);
            generateRoomsInRange(random.nextInt(5, 9), 3, 7,
                    quad.getMinX(), quad.getMaxX(),
                    quad.getMinY(), quad.getMaxY(), true);
        }
    }

    private ArrayList<Room> generateRoomsInRange(int numRooms, int minLength, int maxLength,
                                                 int minX, int maxX, int minY, int maxY, boolean prune)  {
        ArrayList<Room> newRooms = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            int randRoomWidth = random.nextInt(minLength, maxLength);
            int randRoomHeight = random.nextInt(minLength, maxLength);

            int randRoomX = random.nextInt(minX, maxX + 1 - randRoomWidth);
            int randRoomY = random.nextInt(minY, maxY + 1 - randRoomHeight);

            boolean connected = false;
            boolean overlapping = false;
            for (int x = randRoomX; x < randRoomX + randRoomWidth; x++) {
                if (connected) {
                    break;
                }
                for (int y = randRoomY; y < randRoomY + randRoomHeight; y++) {
                    if (world[x][y].isRoom()) {
                        overlapping = true;
                    }
                    if (world[x][y].isFloor()) {
                        connected = true;
                        break;
                    }
                }
            }
            if (prune && (!connected || overlapping)) {
                i--; // Don't count the failed room placement
                continue;
            }
            for (int x = randRoomX; x < randRoomX + randRoomWidth; x++) {
                for (int y = randRoomY; y < randRoomY + randRoomHeight; y++) {
                    world[x][y].makeRoom();
                }
            }
        }
        return newRooms;
    }

    private Path generatePathOnWorld(PathParams pathParams, int delayMillis) {
        Path path = new Path(pathParams);
        world[pathParams.getStartX()][pathParams.getStartY()].makeFloor();
        path.add(world[pathParams.getStartX()][pathParams.getStartY()]);
        int maxNumSteps = pathParams.getMaxNumSteps();
        int minStepLength = pathParams.getMinStepLength();
        int maxStepLength = pathParams.getMaxStepLength();
        int failed = 0;
        for (int i = 0; i < maxNumSteps; i++) {
            if (failed > 30) {
//                world[path.getHead().getX()][path.getHead().getY()].makeFlower();
                path.moveHead(random);
                failed = 0;
                i--; // Don't count this ultra stuck spot
                continue;
            }
            int randDirection = random.nextInt(4);
            int randStepLength = random.nextInt(minStepLength, maxStepLength);
            if (!stepInBounds(path, randDirection, randStepLength)) {
                i--; // Ignore this attempt and try again
                failed++;
//                System.out.println("Out of bounds step of length " + randStepLength + " in direction " + randDirection);
                continue;
            }
            if (!validStep(path, randDirection, randStepLength)) {
                i--; // Ignore this attempt and try again
                failed++;
//                System.out.println("Invalid step of length " + randStepLength + " in direction " + randDirection);
                continue;
            }
            takeStep(path, randDirection, randStepLength);
            failed = 0;
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

    public void addPlayer() {
        boolean placedPlayer = false;
        while (!placedPlayer) {
            int randX = random.nextInt(0, width);
            int randY = random.nextInt(0, height);
            if (world[randX][randY].isFloor()) {
                player = new Player(randX, randY);
                player.sittingOn(world[randX][randY].getTETile());
                world[randX][randY].makePlayer();

                placedPlayer = true;
            }
        }
    }
}
