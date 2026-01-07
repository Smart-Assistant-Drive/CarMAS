package env;

public class Scaler {

    private final double scale;

    public Scaler(double scale){
        this.scale = scale;
    }

    public double scaleValue(double value){
        return value * scale;
    }

    public double descaleValue(double value){
        return value / scale;
    }
}
