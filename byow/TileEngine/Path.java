package byow.TileEngine;

import java.util.ArrayList;

public class Path {
    private ArrayList<WorldTile> path;
    private WorldTile head;

    private int walkIndex;

    public Path() {
        path = new ArrayList<>();
        head = null;
        walkIndex = -1;
    }

    public void add(WorldTile worldTile) {
        path.add(worldTile);
        head = worldTile;
    }

    public void startWalk() {
        walkIndex = 0;
    }

    public boolean hasNextTile() {
        return walkIndex < path.size();
    }

    public WorldTile nextTile() {
        if (!hasNextTile()) {
            throw new RuntimeException("No more tiles in path.");
        }
        WorldTile nextTile = path.get(walkIndex);
        incrementWalkIndex();
        return nextTile;
    }

    private void incrementWalkIndex() {
        walkIndex++;
    }
}
