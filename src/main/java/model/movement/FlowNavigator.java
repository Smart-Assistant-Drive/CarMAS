package model.movement;

import model.RoadPosition;
import model.math.Point;
import model.math.Points;
import model.math.Vector;
import model.path.Flow;

import static model.math.Points.vector;

public class FlowNavigator {

    private final Flow flow;
    private RoadPosition position;

    public FlowNavigator(Flow flow, Point initialPoint) {
        this.flow = flow;

        int segIndex = Points.findContainingSegment(flow.getPath(), initialPoint);
        double offset = Points.distance(flow.getPath()[segIndex], initialPoint);

        this.position = new RoadPosition(segIndex, offset);
    }

    public Flow getFlow() {
        return flow;
    }

    public RoadPosition getPosition() {
        return position;
    }

    public boolean isAtEndOfFlow() {
        return position.getSegmentIndex() >= flow.getPath().length - 1;
    }

    public Point getSegmentStart() {
        return flow.getPath()[position.getSegmentIndex()];
    }

    public Point getCarProjection() {
        return flow.getPath()[position.getSegmentIndex()];
    }

    public Vector distanceFromSegmentStart(Point carPos) {
        return vector(getSegmentStart(), carPos);
    }

    public Point[] getPath() {
        return flow.getPath();
    }

    public void updatePosition(double step) {
        position.advance(step);
    }

    public void nextSegment() {
        position.nextSegment();
    }
}