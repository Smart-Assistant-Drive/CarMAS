package env.car_action;

import env.CarAction;
import model.Car;

public class PassedStopAction implements CarAction {

    private final Runnable handler;

    public PassedStopAction(Runnable handler) {
        this.handler = handler;
    }

    @Override
    public boolean execute(Car car) {
        handler.run();
        return true;
    }
}
