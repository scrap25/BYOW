package byow.TileEngine;

import java.util.ArrayList;

public class Path {
    private ArrayList<WorldTile> path;
    private WorldTile head;

    public Path() {
        path = new ArrayList<>();
        head = null;
    }

    public void add(WorldTile worldTile) {
        path.add(worldTile);
        head = worldTile;
    }
}
