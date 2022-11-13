package byow.TileEngine;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Main {

    private static final long DEFAULT_SEED = 2873123;
    private static final int WIDTH = 80;
    private static final int HEIGHT = 44;

    public static void main(String[] args) {
        Main main = new Main();
        main.setupFrame();
        main.startGameLoop();
    }

    private void setupFrame() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);//canvas size
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void startGameLoop() {
        Long seed = 2324l;
        if (seed == null) {
            drawText("Enter seed:");

            seed = readSeed();
            System.out.println("Seed: " + seed);

            drawText("... Generating world with seed: " + seed + " ...");

            StdDraw.pause(1000);
        }
        WorldHandler worldHandler = new WorldHandler(WIDTH, HEIGHT, seed);
        worldHandler.addPlayer();
        worldHandler.renderWorld();

        StdDraw.pause(5000);
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
                } else if (seed.length() > 0 && curr == 'S' || curr == 's') {
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
    
    public void drawText(String text) {
        StdDraw.clear(Color.BLACK);//background
        StdDraw.setPenColor(Color.WHITE);//color of words
        Font fontBig = new Font("Monaco", Font.BOLD, 30);

        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, text);
        StdDraw.show();
    }
    
}
