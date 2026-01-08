package model;

import model.math.Point;
import model.math.Points;
import model.math.Vector;
import model.movement.FlowNavigator;
import model.movement.MovementEventDispatcher;
import model.movement.PathNavigator;
import model.path.Flow;
import model.path.Path;
import model.path.PathSegment;

import java.util.ArrayList;
import java.util.List;

import static model.math.Points.vector;

public class MovementEngine {

    private final Car car;
    private final PathNavigator pathNav;
    private FlowNavigator flowNav;

    private final List<MovementListener> listeners = new ArrayList<>();
    private final MovementEventDispatcher dispatcher;

    public MovementEngine(Car car, Path path) {
        this.car = car;
        this.pathNav = new PathNavigator(path);

        PathSegment first = pathNav.getCurrent();
        Flow initialFlow = first.getFlow();

        this.flowNav = new FlowNavigator(initialFlow, car.getPosition());
        this.dispatcher = new MovementEventDispatcher(listeners);

        dispatcher.notifyRoadChanged(
                null,
                initialFlow,
                first,
                flowNav.getPosition(),
                pathNav.getIndex(),
                car,
                flowNav.distanceFromSegmentStart(car.getPosition())
        );
    }

    public void addListener(MovementListener ml) {
        listeners.add(ml);
    }

    public void move(double distance) {

        while (distance > 0) {

            Point[] path = flowNav.getPath();
            RoadPosition pos = flowNav.getPosition();

            // Fine flow → next PathSegment
            if (flowNav.isAtEndOfFlow()) {
                if (!advanceFlow()) return;
                continue;
            }

            Point a = path[pos.getSegmentIndex()];
            Point b = path[pos.getSegmentIndex() + 1];

            double len = Points.distance(a, b);
            double remain = len - pos.getOffset();

            double step = Math.min(distance, remain);

            Vector dir = vector(a, b).normalize();
            Point newPos = new Point(
                    car.getPosition().getX() + dir.getX() * step,
                    car.getPosition().getY() + dir.getY() * step
            );
            car.setPosition(newPos);

            flowNav.updatePosition(step);
            distance -= step;

            if (pos.getOffset() >= len - 1e-9)
                flowNav.nextSegment();

            if (Points.distance(car.getPosition(), pathNav.getCurrent().getEnd()) < 1e-6) {
                advanceFlow();
                continue;
            }

            dispatcher.notifyMovementUpdated(
                    flowNav.getFlow(),
                    car,
                    flowNav.getPosition(),
                    pathNav.getIndex(),
                    flowNav.distanceFromSegmentStart(car.getPosition())
            );
        }
    }

    private boolean advanceFlow() {

        Flow oldFlow = flowNav.getFlow();
        PathSegment oldSegment = pathNav.getCurrent();
        int oldIndex = pathNav.getIndex();

        PathSegment next = pathNav.advance();
        if (next == null) {
            dispatcher.notifyRoadChanged(
                    oldFlow, null, null,
                    flowNav.getPosition(), oldIndex,
                    car, flowNav.distanceFromSegmentStart(car.getPosition())
            );
            return false;
        }

        Flow newFlow = next.getFlow();
        car.setPosition(next.getStart());
        flowNav = new FlowNavigator(newFlow, car.getPosition());

        dispatcher.notifyRoadChanged(
                oldFlow, newFlow, next,
                flowNav.getPosition(), oldIndex,
                car, flowNav.distanceFromSegmentStart(car.getPosition())
        );

        return true;
    }

    public Flow getCurrentFlow() {
        return flowNav.getFlow();
    }

    public int getCurrentFlowSegmentIndex() {
        return flowNav.getPosition().getSegmentIndex();
    }

    public Vector getDistanceFromSegmentStart() {
        return flowNav.distanceFromSegmentStart(car.getPosition());
    }

    public RoadPosition getCurrentRoadPosition() {
        return flowNav.getPosition();
    }

    public Point getDestination() {
        return pathNav.getDestination();
    }
}