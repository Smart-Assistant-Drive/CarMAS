package env.car_action;

import env.CarAction;
import model.Car;

public class BrakeAction implements CarAction {
    @Override
    public boolean execute(Car car) {
        car.setSpeed(Math.max(0, car.getSpeed() - 5));
        return true;
    }
}
