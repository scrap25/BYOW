package byow.TileEngine;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class GameEngine {

    private static final long DEFAULT_SEED = 2873123;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 45;

    private boolean gameOver;

    private WorldHandler worldHandler;

    private GameLoader gameLoader;
    private boolean stopMusic;

    public void setupFrame() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);//canvas size
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void startGameLoop(Long seed, boolean render) {
        playInBackground("start.wav");

        gameLoader = new GameLoader();
        char menuSelection = getMenuSelection();
        switch (menuSelection) {
            case 'L':
                boolean hasPreviousGame = gameLoader.loadGameFromFile();
                if (hasPreviousGame) {
                    seed = gameLoader.getSeed();
                    worldHandler = new WorldHandler(WIDTH, HEIGHT, seed, false);
                    worldHandler.addPlayer();
                    String previousKeyboardInputs = gameLoader.getKeyHistory();
                    for (int i = 0; i < previousKeyboardInputs.length(); i++) {
                        worldHandler.movePlayer("" + previousKeyboardInputs.charAt(i));
                    }
                    break;
                }
            case 'N':
                if (seed == null) {
                    drawText("Enter seed:");

                    seed = readSeed();
                    System.out.println("Seed: " + seed);

                    drawText("... Generating world with seed: " + seed + " ...");

                    StdDraw.pause(1000);
                }
                gameLoader.clearSavedGame();
                gameLoader.setSeed(seed);
                worldHandler = new WorldHandler(WIDTH, HEIGHT, seed, false);
                worldHandler.addPlayer();
                break;
            case 'Q':
                System.exit(0);
                break;
            default:
                throw new RuntimeException("Invalid menu option.");
        }

        playInBackground("game.wav");

        if (render) {
            worldHandler.renderWorld();
            gameOver = false;
            boolean worldChanged = false;
            String tileDesc = "";
            boolean readyToQuit = false;
            while (!gameOver) {
                Character c = readCharFromKeyboard();
                if (c == null) {
                    c = ' ';
                }
                String keyChar = "" + c;
                if ("wasdWASD".contains(keyChar)) {
                    worldHandler.movePlayer(keyChar);
                    worldChanged = true;
                    readyToQuit = false;
                    gameLoader.record(keyChar);
                } else if ("tT".contains(keyChar)) {
                    worldHandler.toggleVisibility();
                    worldChanged = true;
                } else if (":".equals(keyChar)){
                    readyToQuit = true;
                } else if ("qQ".contains(keyChar)) {
                    if (readyToQuit) {
                        gameLoader.saveGame();
                        System.exit(0);
                        break;
                    }
                    readyToQuit = false;
                }
                // Update the Heads Up Display with whatever the mouse is hovering over
                String newTileDesc = worldHandler.getTileDescAt((int) StdDraw.mouseX(), (int) StdDraw.mouseY());
                if (newTileDesc != null && !newTileDesc.equals(tileDesc)) {
                    tileDesc = newTileDesc;
                    System.out.println("tileDesc = " + tileDesc);
                    worldChanged = true;
                }

                if (worldChanged) {
                    System.out.println("World changed so rendering again.");
                    worldHandler.renderWorld();
//                    StdDraw.pause(50);
                    drawHeadsUpDisplay(tileDesc + "     Keys collected: " + worldHandler.getKeysCollected());
                    worldChanged = false;
                }
            }
        }
    }

    private void stopMusicInBackground() {
        stopMusic = true;
    }
    private void playInBackground(String fileName) {
        stopMusicInBackground();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopMusic = false;
        Thread musicThread = new Thread(() -> runMusic("byow/music/" + fileName));
        musicThread.start();
    }

    private void runMusic(String fileName) {
        double[] allBytes = StdAudio.read(fileName);
        int segmentSize = 1;
        for (int i = 0; i < allBytes.length; i+=segmentSize) {
            StdAudio.play(allBytes[i]);
            if (stopMusic) {
                return;
            }
        }
    }

    private Character getMenuSelection() {
        StdDraw.clear(Color.BLACK);//background
        StdDraw.setPenColor(Color.orange);//color of words
        StdDraw.picture(WIDTH/2,HEIGHT/2,"./byow/pic/61bstaff.png",WIDTH,HEIGHT);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);

        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, 3.7 * HEIGHT / 5, "Please choose menu option: ");
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 5, "New Game (N)");
        StdDraw.text(WIDTH / 2, 2 * HEIGHT / 5, "Load Game (L)");
        StdDraw.text(WIDTH / 2, 1 * HEIGHT / 5, "Quit Game (Q)");

        StdDraw.show();
        Character c = null;
        while (c == null) {
            c = readCharFromKeyboard();
            if ("NLQnlq".contains("" + c)) {
                return Character.toUpperCase(c);
            }
            c = null;
        }
        return null;
    }

    private Character readCharFromKeyboard() {
        if (StdDraw.hasNextKeyTyped()){
            return StdDraw.nextKeyTyped();
        }
        return null;
    }

    public Long readSeed() {
        String seed = "";
        boolean seedStarted = false;
        String input = "";
        while (true) {
            if(StdDraw.hasNextKeyTyped()){
                char curr=StdDraw.nextKeyTyped();
                if (curr == 'q') {
                    return null;
                }
                if (!seedStarted && (curr == 'N' || curr == 'n')) {
                    seedStarted = true;
                    input += curr;
                } else if (seed.length() > 0 && (curr == 'S' || curr == 's')) {
                    input += curr;
                    drawText(seed);
                    break;
                } else if (seedStarted && Character.isDigit(curr)) {
                    seed += curr;
                    input += curr;
                }
                drawText(input);
            }
        }

        return Long.parseLong(seed);
    }

    public void drawHeadsUpDisplay(String text) {
        StdDraw.setPenColor(Color.WHITE);//color of words
        Font fontSmall = new Font("Monaco", Font.BOLD, 10);
        StdDraw.setFont(fontSmall);
        StdDraw.text(10, HEIGHT - 1, text);
        StdDraw.show();
    }

    public void drawText(String text) {
        StdDraw.clear(Color.BLACK);//background
        StdDraw.setPenColor(Color.WHITE);//color of words
        Font fontBig = new Font("Monaco", Font.BOLD, 30);

        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, text);
        StdDraw.show();
    }

    public TETile[][] getWorldAsTETile() {
        return worldHandler.getWorldAsTETile();
    }

    public String getWorldAsString() {
        return worldHandler.toString();
    }
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.setupFrame();
        gameEngine.startGameLoop(4456l, true);
    }


}
