package byow.TileEngine;

public class PathParams {

    private int maxNumSteps;
    private int minStepLength;
    private int maxStepLength;

    private int startX;
    private int startY;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;


    public PathParams(int maxNumSteps, int minStepLength, int maxStepLength, int startX, int startY, int minX, int maxX, int minY, int maxY) {
        this.maxNumSteps = maxNumSteps;
        this.minStepLength = minStepLength;
        this.maxStepLength = maxStepLength;
        this.startX = startX;
        this.startY = startY;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int getMaxNumSteps() {
        return maxNumSteps;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxStepLength() {
        return maxStepLength;
    }

    public int getMinStepLength() {
        return minStepLength;
    }
}
