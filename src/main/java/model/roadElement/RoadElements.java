package model.roadElement;

import java.util.List;
import java.util.Optional;

public class RoadElements {


    public static Optional<SpeedLimitSign> getLastSpeedLimitSign(List<RoadElement> roadElements) {
        for(int i = roadElements.size() - 1; i >= 0; i--) {
            RoadElement re = roadElements.get(i);
            if(re instanceof SpeedLimitSign) {
                return Optional.of((SpeedLimitSign) re);
            }
        }
        return Optional.empty();
    }

    public static Optional<Sign> getNextStopSign(List<RoadElement> roadElements) {
        for(RoadElement re : roadElements) {
            if(re instanceof Sign sign && !sign.isUsed() && sign.getType() == Sign.SignType.STOP) {
                return Optional.of(sign);
            }
        }
        return Optional.empty();
    }

    public static Optional<TrafficLight> getNextTrafficLight(List<RoadElement> roadElements) {
        for(RoadElement re : roadElements) {
            if(re instanceof TrafficLight tl) {
                return Optional.of(tl);
            }
        }
        return Optional.empty();
    }
}
