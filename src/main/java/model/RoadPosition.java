package model;

public class RoadPosition {
    private int segmentIndex;     // indice del segmento nella polyline
    private double offset;        // distanza percorsa nel segmento

    public RoadPosition(int segmentIndex, double offset) {
        this.segmentIndex = segmentIndex;
        this.offset = offset;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public double getOffset() {
        return offset;
    }

    public void advance(double delta) {
        offset += delta;
    }

    public void nextSegment() {
        segmentIndex++;
        offset = 0;
    }
}