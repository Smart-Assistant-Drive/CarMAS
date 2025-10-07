package model.math;

public class Points {

    public static final Point ORIGIN = new Point(0, 0);
    public static final Point UNIT_X = new Point(1, 0);
    public static final Point UNIT_Y = new Point(0, 1);

    public static double distance(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Vector vector(Point from, Point to) {
        return new Vector(to.getX() - from.getX(), to.getY() - from.getY());
    }
}
