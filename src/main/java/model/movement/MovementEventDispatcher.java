package model.movement;

import model.Car;
import model.MovementListener;
import model.RoadPosition;
import model.math.Vector;
import model.path.Flow;
import model.path.PathSegment;

import java.util.List;

public class MovementEventDispatcher {

    private final List<MovementListener> listeners;

    public MovementEventDispatcher(List<MovementListener> listeners) {
        this.listeners = listeners;
    }

    public void notifyRoadChanged(
            Flow oldFlow, Flow newFlow,
            PathSegment newSegment, RoadPosition pos,
            int indexFlow, Car car, Vector dist
    ) {
        for (MovementListener l : listeners) {
            l.onRoadChanged(oldFlow, newFlow, newSegment, pos, car, indexFlow, dist);
        }
    }

    public void notifyMovementUpdated(
            Flow flow, Car car, RoadPosition pos,
            int pathIndex, Vector dist
    ) {
        for (MovementListener l : listeners) {
            l.onMovementUpdated(flow, car, pos, pathIndex, dist);
        }
    }
}