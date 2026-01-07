package model.roadElement;

import model.math.Point;
import model.path.Flow;

public class TrafficLight implements RoadElement {

    public enum State {
        RED,
        YELLOW,
        GREEN
    }

    private final String id;
    private State state;
    private final Point position;
    private final Flow flow; // Identifier for the street this semaphore belongs to

    public TrafficLight(String id, Point position , Flow flow) {
        this.id = id;
        this.flow = flow;
        this.position = position;
        this.state = State.RED; // Default state
    }

    public String getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Point getPosition() {
        return position;
    }

    public Flow getRoad() {
        return flow;
    }
}
