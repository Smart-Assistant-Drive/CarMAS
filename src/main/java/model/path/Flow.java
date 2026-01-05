package model.path;

import model.math.Point;

public class Flow {

    private final Point[] path;

    public Flow(Point[] path) {
        this.path = path;
    }

    public Point[] getPath() {
        return path;
    }
}
