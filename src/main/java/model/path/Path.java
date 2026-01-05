package model.path;

import java.util.List;

public class Path {
    private final List<PathSegment> segments;

    public Path(List<PathSegment> segments) {
        this.segments = segments;
    }

    public List<PathSegment> getSegments() {
        return List.copyOf(segments);
    }
}
