package model;

import model.math.Point;
import model.math.Points;
import model.math.Vector;
import model.path.Path;
import model.path.PathSegment;
import model.path.Flow;

import java.util.ArrayList;
import java.util.List;

import static model.math.Points.vector;

public class Movement {

    private final Car car;
    private final Path path;

    private int pathIndex = 0;
    private PathSegment currentSegment;
    private Flow currentFlow;
    private RoadPosition position;

    private final List<MovementListener> listeners = new ArrayList<>();

    public Movement(Car car, Path path) {
        this.car = car;
        this.path = path;

        currentSegment = path.getSegments().get(0);
        currentFlow = currentSegment.getRoad();

        int segIndex = Points.findContainingSegment(
                currentFlow.getPath(), car.getPosition()
        );

        position = new RoadPosition(segIndex, 0);
    }

    public void addListener(MovementListener listener) {
        listeners.add(listener);
    }

    public Flow getCurrentRoad() {
        return currentFlow;
    }

    public RoadPosition getPosition() {
        return position;
    }

    public void move(double distance) {
        while (distance > 0) {

            Point[] pts = currentFlow.getPath();

            // fine road → prossimo PathSegment
            if (position.getSegmentIndex() >= pts.length - 1) {
                if (!advanceToNextPathSegment()) return;
                continue;
            }

            Point a = pts[position.getSegmentIndex()];
            Point b = pts[position.getSegmentIndex() + 1];

            double segmentLength = Points.distance(a, b);
            double remaining = segmentLength - position.getOffset();

            double step = Math.min(distance, remaining);

            Vector dir = vector(a, b).normalize();
            car.setPosition(new Point(
                    car.getPosition().getX() + dir.getX() * step,
                    car.getPosition().getY() + dir.getY() * step
            ));

            position.advance(step);
            distance -= step;

            if (position.getOffset() >= segmentLength - 1e-9) {
                position.nextSegment();
            }

            double distToEnd = Points.distance(
                    car.getPosition(),
                    currentSegment.getEnd()
            );

            if (distToEnd < 1e-6) {
                advanceToNextPathSegment();
            }
        }
    }

    private boolean advanceToNextPathSegment() {
        pathIndex++;
        if (pathIndex >= path.getSegments().size()) {
            return false;
        }

        Flow oldFlow = currentFlow;

        currentSegment = path.getSegments().get(pathIndex);
        currentFlow = currentSegment.getRoad();
        position = new RoadPosition(0, 0);
        car.setPosition(currentSegment.getStart());

        notifyRoadChanged(oldFlow, currentFlow);
        return true;
    }

    private void notifyRoadChanged(Flow oldFlow, Flow newFlow) {
        for (MovementListener l : listeners) {
            l.onRoadChanged(oldFlow, newFlow, currentSegment);
        }
    }
}