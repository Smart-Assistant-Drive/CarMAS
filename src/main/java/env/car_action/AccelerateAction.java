package env.car_action;

import env.CarAction;
import model.Car;

public class AccelerateAction implements CarAction {
    @Override
    public boolean execute(Car car) {
        car.setSpeed(car.getSpeed() + 10);
        return true;
    }
}
