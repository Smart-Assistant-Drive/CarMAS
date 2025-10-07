package model.math;

public class Vectors {

    public static final Vector ZERO = new Vector(0, 0);
    public static final Vector UNIT_X = new Vector(1, 0);
    public static final Vector UNIT_Y = new Vector(0, 1);

    public static Vector add(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }

    public static Vector subtract(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(), v1.getY() - v2.getY());
    }

    public static Vector scale(Vector v, double scalar) {
        return new Vector(v.getX() * scalar, v.getY() * scalar);
    }

    public static double dot(Vector v1, Vector v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }
}
