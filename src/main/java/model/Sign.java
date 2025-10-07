package model;

import model.math.Point;

public class Sign implements RoadElement  {

    private final Point position;
    private final int streetId; // Identifier for the street this sign belongs to
    private final String type; // Type of sign (e.g., "STOP", "YIELD", "SPEED_LIMIT")

    public Sign(Point position, int streetId, String type) {
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

    public String getType() {
        return type;
    }
}
