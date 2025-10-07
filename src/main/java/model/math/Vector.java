package model.math;

public class Vector {

    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public double getMagnitude() {
        return Math.sqrt(lengthSquared());
    }

    public Vector normalize() {
        double magnitude = getMagnitude();
        if (magnitude == 0) {
            return new Vector(0, 0);
        }
        return new Vector(x / magnitude, y / magnitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector vector = (Vector) obj;
        return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        return result;
    }
}
