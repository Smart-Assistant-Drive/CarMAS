package env;

public class Timer {

    private long startTime;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }
}
