package model;

import model.math.Vector;
import model.path.PathSegment;
import model.path.Flow;

public interface MovementListener {

    void onRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment newSegment,
            Car car,
            int indexFlow,
            Vector distance
    );
}
