package model;

import model.math.Vector;
import model.path.Flow;

public class CarUpdate {

    private final Car car;
    private final Flow flow;
    private final int indexFlow;
    private final Vector distance;

    public CarUpdate(Car car, Flow flow, int indexFlow, Vector distance) {
        this.car = car;
        this.flow = flow;
        this.indexFlow = indexFlow;
        this.distance = distance;
    }

    public Car getCar() {
        return car;
    }

    public Flow getFlow() {
        return flow;
    }

    public int getIndexFlow() {
        return indexFlow;
    }

    public Vector getDistance() {
        return distance;
    }
}
