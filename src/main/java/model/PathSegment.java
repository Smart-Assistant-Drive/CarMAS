package model;

import model.math.Point;

public class PathSegment {
    private final Point start;
    private final Point end;
    private final Road road;

    public PathSegment(Road road, Point start, Point end) {
        this.road = road;
        this.start = start;
        this.end = end;
    }

    public Road getRoad() {
        return road;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
