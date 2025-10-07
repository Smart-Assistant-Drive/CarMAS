package model;

import model.math.Point;

public class Road {

    private final Point[] path;

    public Road(Point[] path) {
        this.path = path;
    }

    public Point[] getPath() {
        return path;
    }
}
