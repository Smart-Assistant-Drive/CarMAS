package model;

import model.math.Point;

public class Car {

    private Point position;
    private double speed; // in units per second

    public Car(Point position, double speed) {
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
}
