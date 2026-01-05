package model.roadElement;

import model.math.Point;
import model.path.Flow;

public interface RoadElement {

    Flow getRoad();

    Point getPosition();
}
