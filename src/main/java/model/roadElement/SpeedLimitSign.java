package model.roadElement;

import model.math.Point;
import model.path.Flow;

public class SpeedLimitSign extends Sign {

    private final int speedLimit;

    public SpeedLimitSign(Point position, Flow flow, int speedLimit) {
        super(position, flow, SignType.SPEED_LIMIT);
        this.speedLimit = speedLimit;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }
}
