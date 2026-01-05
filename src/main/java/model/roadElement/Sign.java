package model.roadElement;

import model.math.Point;
import model.path.Flow;

public class Sign implements RoadElement  {

    public enum SignType {
        STOP,
        YIELD,
        SPEED_LIMIT
    }

    private final Point position;
    private final Flow flow; // Identifier for the street this sign belongs to
    private final SignType type; // Type of sign (e.g., "STOP", "YIELD", "SPEED_LIMIT")
    private boolean used = false;

    public Sign(Point position, Flow flow, SignType type) {
        this.position = position;
        this.flow = flow;
        this.type = type;
    }

    public Point getPosition() {
        return position;
    }

    public Flow getRoad() {
        return flow;
    }

    public SignType getType() {
        return type;
    }

    public boolean isUsed() {
        return used;
    }

    public void useSignal() {
        this.used = true;
    }
}
