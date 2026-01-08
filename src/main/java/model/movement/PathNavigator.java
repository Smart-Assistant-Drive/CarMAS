package model.movement;

import model.math.Point;
import model.path.Path;
import model.path.PathSegment;

public class PathNavigator {

    private final Path path;
    private int index = 0;

    public PathNavigator(Path path) {
        this.path = path;
    }

    public PathSegment getCurrent() {
        return path.getSegments().get(index);
    }

    public int getIndex() {
        return index;
    }

    public boolean hasNext() {
        return index + 1 < path.getSegments().size();
    }

    public PathSegment advance() {
        index++;
        if (index >= path.getSegments().size()) return null;
        return path.getSegments().get(index);
    }

    public Point getDestination() {
        int size = path.getSegments().size();
        if (size == 0) return null;
        return path.getSegments().get(size - 1).getEnd();
    }
}