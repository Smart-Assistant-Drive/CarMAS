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

        currentSegment = path.getSegments().getFirst();
        currentFlow = currentSegment.getFlow();

        int segIndex = Points.findContainingSegment(
                currentFlow.getPath(), car.getPosition()
        );

        position = new RoadPosition(segIndex, 0);
        Vector initialDistance = vector(
                currentFlow.getPath()[segIndex],
                car.getPosition()
        );
        notifyRoadChanged(null, currentFlow, currentSegment, pathIndex, initialDistance);
    }

    public void addListener(MovementListener listener) {
        listeners.add(listener);
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

            boolean roadChanged = false;

            if (distToEnd < 1e-6) {
                roadChanged = advanceToNextPathSegment();
            }


            Point[] roadPath = currentFlow.getPath();
            int lastIndexPassed = position.getSegmentIndex();
            Point lastPassedPoint = roadPath[lastIndexPassed];

            Vector distanceFromLastPassed = vector(
                    lastPassedPoint,
                    car.getPosition()
            );

            if (!roadChanged) {
                notifyMovementUpdated(distanceFromLastPassed);
            }
        }
    }

    private boolean advanceToNextPathSegment() {

        // === SALVATAGGIO STATO REALE ===
        PathSegment oldSegment = currentSegment;
        Flow oldFlow = oldSegment.getFlow();
        int oldPathIndex = pathIndex;

        Point[] roadPath = currentFlow.getPath();
        int lastIndexPassed = position.getSegmentIndex();

        Point lastPassedPoint = roadPath[lastIndexPassed];
        Point carPos = car.getPosition();

        Vector distanceFromLastPassed = vector(lastPassedPoint, carPos);

        // === AVANZAMENTO PATH ===
        pathIndex++;
        if (pathIndex >= path.getSegments().size()) {
            notifyRoadChanged(
                    oldFlow,
                    null,
                    null,
                    oldPathIndex,
                    distanceFromLastPassed
            );
            return false;
        }

        currentSegment = path.getSegments().get(pathIndex);
        Flow newFlow = currentSegment.getFlow();
        currentFlow = newFlow;

        car.setPosition(currentSegment.getStart());
        position = new RoadPosition(0, 0);

        notifyRoadChanged(
                oldFlow,
                newFlow,
                currentSegment,
                oldPathIndex,
                distanceFromLastPassed
        );

        return true;
    }

    private void notifyRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment newSegment,
            int indexFlow,
            Vector distance
    ) {
        for (MovementListener l : listeners) {
            l.onRoadChanged(
                    previousFlow,
                    newFlow,
                    newSegment,
                    car,
                    indexFlow,
                    distance
            );
        }
    }

    private void notifyMovementUpdated(Vector distanceFromLastPassed) {
        for (MovementListener l : listeners) {
            l.onMovementUpdated(
                    currentFlow,
                    car,
                    position,
                    pathIndex,
                    distanceFromLastPassed
            );
        }
    }
}