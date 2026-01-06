package model;

import model.math.Points;
import model.math.Vector;
import model.path.PathSegment;
import model.path.Flow;
import model.roadElement.RoadElement;

import java.util.ArrayList;
import java.util.List;

public class RoadsElementsVision implements MovementListener {

    private static class CachedElement {
        RoadElement element;
        int segmentIndex;
        double offset;

        CachedElement(RoadElement e) {
            this.element = e;
            Flow flow = e.getRoad();
            this.segmentIndex = Points.findContainingSegment(
                    flow.getPath(), e.getPosition()
            );
            this.offset = Points.distance(
                    flow.getPath()[segmentIndex],
                    e.getPosition()
            );
        }
    }

    private final List<CachedElement> cached = new ArrayList<>();
    private final List<RoadElement> nextElements = new ArrayList<>();
    private final List<RoadElement> passedElements = new ArrayList<>();

    private Flow currentFlow;

    public RoadsElementsVision(List<RoadElement> elements) {
        for (RoadElement e : elements) {
            cached.add(new CachedElement(e));
        }
    }

    @Override
    public void onRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment segment,
            Car car,
            int indexFlow,
            Vector distance
    ) {
        this.currentFlow = newFlow;

        if (newFlow != null) {
            update(new RoadPosition(0, 0));
        } else {
            nextElements.clear();
            passedElements.clear();
        }
    }

    @Override
    public void onMovementUpdated(Flow flow, Car car, RoadPosition carPos, int indexFlow, Vector distance) {
        if (flow != currentFlow) return;
        update(carPos);
    }

    private void update(RoadPosition carPos) {
        nextElements.clear();
        passedElements.clear();

        if (currentFlow == null) return;

        for (CachedElement c : cached) {
            if (c.element.getRoad() != currentFlow) continue;

            if (isPassed(carPos, c)) {
                passedElements.add(c.element);
            } else {
                nextElements.add(c.element);
            }
        }
    }

    public List<RoadElement> getNextRoadElements() {
        return nextElements;
    }

    public List<RoadElement> getPassedRoadElements() {
        return passedElements;
    }

    private boolean isPassed(RoadPosition car, CachedElement elem) {
        if (elem.segmentIndex < car.getSegmentIndex()) return true;
        if (elem.segmentIndex > car.getSegmentIndex()) return false;
        return elem.offset < car.getOffset();
    }
}