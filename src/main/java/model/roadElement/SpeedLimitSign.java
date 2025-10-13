package model.roadElement;

import model.math.Point;

public class SpeedLimitSign extends Sign {

    private final int speedLimit;

    public SpeedLimitSign(Point position, int streetId, int speedLimit) {
        super(position, streetId, SignType.SPEED_LIMIT);
        this.speedLimit = speedLimit;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }
}
