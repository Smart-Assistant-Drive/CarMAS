package env.car_action;

import env.CarAction;
import model.Car;

public class KeepSpeedAction implements CarAction {
    @Override
    public boolean execute(Car car) {
        return true;
    }
}
