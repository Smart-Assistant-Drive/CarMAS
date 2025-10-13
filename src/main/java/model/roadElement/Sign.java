package model.roadElement;

import model.math.Point;

public class Sign implements RoadElement  {

    public enum SignType {
        STOP,
        YIELD,
        SPEED_LIMIT
    }

    private final Point position;
    private final int streetId; // Identifier for the street this sign belongs to
    private final SignType type; // Type of sign (e.g., "STOP", "YIELD", "SPEED_LIMIT")

    public Sign(Point position, int streetId, SignType type) {
        this.position = position;
        this.streetId = streetId;
        this.type = type;
    }

    public Point getPosition() {
        return position;
    }

    public int getStreetId() {
        return streetId;
    }

    public SignType getType() {
        return type;
    }
}
