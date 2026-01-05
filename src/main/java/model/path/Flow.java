package model.path;

import model.math.Point;

public class Flow {
    private final String road;
    private final int direction;
    private final Point[] path;

    public Flow(
            String road,
            int direction,
            Point[] path) {
        this.road = road;
        this.direction = direction;
        this.path = path;
    }

    public String getRoad() {
        return road;
    }

    public int getDirection() {
        return direction;
    }

    public Point[] getPath() {
        return path;
    }
}
