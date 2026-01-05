package model.path;

import model.math.Point;

public class PathSegment {
    private final Point start;
    private final Point end;
    private final Flow flow;

    public PathSegment(Flow flow, Point start, Point end) {
        this.flow = flow;
        this.start = start;
        this.end = end;
    }

    public Flow getRoad() {
        return flow;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
