package model;

import model.math.Vector;
import model.path.PathSegment;
import model.path.Flow;

public interface MovementListener {

    void onRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment newSegment,
            RoadPosition roadPosition,
            Car car,
            int indexFlow,
            Vector distance
    );

    void onMovementUpdated(
            Flow flow,
            Car car,
            RoadPosition carPos,
            int indexFlow,
            Vector distance
    );
}
