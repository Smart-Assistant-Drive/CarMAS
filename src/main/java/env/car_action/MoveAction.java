package env.car_action;

import env.CarAction;
import env.Timer;
import model.Car;
import model.MovementEngine;

public class MoveAction implements CarAction {

    private final Timer envTimer;
    private final MovementEngine movementEngine;

    public MoveAction(Timer envTimer, MovementEngine movementEngine) {
        this.envTimer = envTimer;
        this.movementEngine = movementEngine;
    }

    @Override
    public boolean execute(Car car) {
        long elapsed = envTimer.getElapsedTime();
        envTimer.reset();
        movementEngine.move(car.getSpeed() * elapsed / 3600.0);
        return true;
    }
}
