package model.roadElement;

import model.math.Point;

public class TrafficLight implements RoadElement {

    public enum State {
        RED,
        YELLOW,
        GREEN
    }

    private State state;
    private final Point position;
    private final int streetId; // Identifier for the street this semaphore belongs to

    public TrafficLight(Point position , int streetId) {
        this.streetId = streetId;
        this.position = position;
        this.state = State.RED; // Default state
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

    public int getStreetId() {
        return streetId;
    }
}
