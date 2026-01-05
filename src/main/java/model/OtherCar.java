package model;

public class OtherCar {

    private final double speed; // in units per second
    private final double distance; // in units

    public OtherCar(double speed, double distance) {
        this.speed = speed;
        this.distance = distance;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDistance() {
        return distance;
    }
}
