package env.car_action;

import env.CarAction;
import env.Timer;
import model.Car;
import model.Movement;

public class MoveAction implements CarAction {

    private final Timer envTimer;
    private final Movement movement;

    public MoveAction(Timer envTimer, Movement movement) {
        this.envTimer = envTimer;
        this.movement = movement;
    }

    @Override
    public boolean execute(Car car) {
        long elapsed = envTimer.getElapsedTime();
        envTimer.reset();
        movement.move(car.getSpeed() * elapsed / 3600.0);
        return true;
    }
}
