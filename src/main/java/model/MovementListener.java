package model;

import model.path.PathSegment;
import model.path.Flow;

public interface MovementListener {

    void onRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment newSegment
    );
}
