package model;

import model.math.Point;

public class Car {

    private Point position;
    private double speed; // in units per second
    private final String plate;

    public Car(Point position, double speed, String plate) {
        this.plate = plate;
        this.position = position;
        this.speed = speed;
    }

    public Point getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getPlate() {
        return plate;
    }
}
