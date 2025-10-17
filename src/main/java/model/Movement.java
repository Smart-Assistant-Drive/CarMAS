package model;

import model.math.Point;
import model.math.Points;
import model.math.Vector;
import model.math.Vectors;
import model.roadElement.RoadElement;

import java.util.ArrayList;
import java.util.List;

import static model.math.Points.vector;

public class Movement {
    private final Car car;
    private Road road;
    private List<RoadElement> roadElements;
    private final List<Integer> roadElementsIndex;
    private int index = 0;

    public Movement(Car car, Road road) {
        this.car = car;
        this.road = road;
        Point[] path = road.getPath();
        index = closestIndex(List.of(path), car.getPosition());
        this.roadElementsIndex = new ArrayList<>();
        this.roadElements = new ArrayList<>();
    }

    private static int closestIndex(List<Point> points, Point p) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point a = points.get(i);
            Point b = points.get(i + 1);

            Vector ab = vector(a, b);
            Vector ap = vector(a, p);

            double abLen2 = ab.lengthSquared();
            double t = abLen2 != 0 ? Vectors.dot(ab, ap) / abLen2 : 0;

            if (t >= 0 && t <= 1) {
                // P proietta dentro al segmento
                return i;
            } else if (t < 0) {
                // P sta prima del segmento
                return i;
            }
        }
        // Se P è oltre tutti i segmenti
        return points.size() - 1;
    }

    /*public Point getCurrentPosition() {
        return currentPosition;
    }*/

    public void setRoadElements(List<RoadElement> roadElements) {
        this.roadElements = roadElements;
        for (RoadElement p : roadElements) {
            roadElementsIndex.add(closestIndex(List.of(road.getPath()), p.getPosition()));
        }
    }

    public List<RoadElement> getRoadElements() {
        return roadElements;
    }

    public List<RoadElement> getPassedStreetElements() {
        for (int i = 0; i < roadElementsIndex.size(); i++) {
            if (roadElementsIndex.get(i) > index) {
                return roadElements.subList(0, i);
            }
        }
        return roadElements;
    }

    public List<RoadElement> getNextStreetElements() {
        for (int i = 0; i < roadElementsIndex.size(); i++) {
            if (roadElementsIndex.get(i) > index) {
                return roadElements.subList(i, roadElements.size());
            }
        }
        return List.of();
    }

    public void move(double distance) {
        Point[] path = road.getPath();
        while (distance > 0 && index < path.length - 1) {
            Point currentPosition = car.getPosition();
            Point nextPoint = path[index + 1];
            double segmentLength = Points.distance(currentPosition, nextPoint);

            if (distance < segmentLength) {
                Vector direction = vector(currentPosition, nextPoint).normalize();
                car.setPosition(new Point(
                        currentPosition.getX() + direction.getX() * distance,
                        currentPosition.getY() + direction.getY() * distance
                ));
                distance = 0;
            } else {
                car.setPosition(nextPoint);
                distance -= segmentLength;
                index++;
            }
        }
    }

    public void setRoad(Road road) {
        this.road = road;
    }
}
