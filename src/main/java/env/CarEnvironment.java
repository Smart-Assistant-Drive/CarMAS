package env;

import gui.GUI;
import gui.GUIEventInterface;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import model.Car;
import model.Movement;
import model.roadElement.RoadElement;
import model.roadElement.RoadElements;
import model.roadElement.Sign;
import model.math.Point;
import model.Road;
import model.roadElement.SpeedLimitSign;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static model.LiteralConverter.*;

public class CarEnvironment extends Environment {

    static Logger logger = Logger.getLogger(CarEnvironment.class.getName());

    private Movement movement;
    private String licenseNumber = "";
    private final Car car = new Car(new Point(0, 0), 0);
    private final Optional<Car> otherCar = //Optional.of(new Car(new Point(10_000, 10_000), 0));
            Optional.empty();
    private GUI gui = null;

    @Override
    public void init(final String[] args) {
        logger.info("Initializing environment...");
        CarEnvironment env = this;
        gui = new GUI(new GUIEventInterface() {
            @Override
            public void onInsertLicense(String licenseNumber) {
                env.licenseNumber = licenseNumber;
            }

            @Override
            public void onStartCar() {
                logger.info("Car started with license: " + env.licenseNumber);
                removePercept(Literal.parseLiteral("car_stopped"));
                addPercept(Literal.parseLiteral("car_started"));
            }

            @Override
            public void onStopCar() {
                logger.info("Car stopped.");
                removePercept(Literal.parseLiteral("car_started"));
                addPercept(Literal.parseLiteral("car_stopped"));
            }

            @Override
            public void onSpeedChange(double newSpeed) {
                car.setSpeed(newSpeed);
                updatePercepts();
                logger.info("Car speed changed to: " + newSpeed);
            }

            @Override
            public void onToggleAutonomousModeChange(boolean isAutonomous) {
                if (isAutonomous) {
                    logger.info("Autonomous mode enabled.");
                    removePercept(Literal.parseLiteral("driver_mode"));
                    addPercept(Literal.parseLiteral("autonomous_mode"));
                } else {
                    logger.info("Manual mode enabled.");
                    removePercept(Literal.parseLiteral("autonomous_mode"));
                    addPercept(Literal.parseLiteral("driver_mode"));
                }

            }
        });
        SwingUtilities.invokeLater(() -> gui.setVisible(true));

        movement = new Movement(
                car,
                new Road(new Point[]{
                        new Point(0, 0),
                        new Point(200, 0),
                        new Point(200, 200),
                        new Point(0, 200)
                })
        );
        movement.setRoadElements(
                Stream.of(
                        new Point(200, 0)
                ).map(
                        p -> (RoadElement) new Sign(p, 0, Sign.SignType.STOP)
                ).toList()
        );
        var speedLimit = speedLimitToLiteral(
                RoadElements.getLastSpeedLimitSign(
                                movement.getPassedStreetElements()
                        )
                        .map(SpeedLimitSign::getSpeedLimit)
                        .orElse(60)
        );
        addPercept(
                speedLimit
        );
        logger.info("speed limit added: " + speedLimit);
        var currentSpeed = currentSpeedToLiteral(car.getSpeed());
        addPercept(currentSpeed);
        logger.info("current speed added: " + currentSpeed);
        addPercept(positionToLiteral(car.getPosition()));
        logger.info("position added");

        otherCar.ifPresent(value -> {
            var otherCarLiteral = otherCarToLiteral(value);
            addPercept(otherCarLiteral);
        });
        logger.info("other car added: " + otherCar);

        logger.info("Finished environment...");
    }

    private void updatePercepts() {
        //clearPercepts();
        List<Literal> actualPercepts = new ArrayList<>();
        final Point pos = car.getPosition();
        logger.info(String.format("Current position: (%.2f, %.2f)", pos.getX(), pos.getY()));
        removePerceptsByUnif(Literal.parseLiteral(POSITION_BASE));
        //removePercept();
        addPercept(positionToLiteral(pos));
        //removePerceptsByUnif(Literal.parseLiteral("next_street_element(X, Y)"));
        /*
        movement.getNextStreetElements().forEach(
                p -> addPercept(Literal.parseLiteral(String.format("next_street_element(%.2f, %.2f)", p.getPosition().getX(), p.getPosition().getY())))
        );*/
        removePerceptsByUnif(Literal.parseLiteral(CURRENT_SPEED_BASE));
        addPercept(currentSpeedToLiteral(car.getSpeed()));
        gui.changeSpeed(car.getSpeed());
        removePerceptsByUnif(Literal.parseLiteral(SPEED_LIMIT_BASE));
        var speedLimitLiteral = speedLimitToLiteral(
                RoadElements.getLastSpeedLimitSign(
                                movement.getPassedStreetElements()
                        )
                        .map(SpeedLimitSign::getSpeedLimit)
                        .orElse(60)
        );
        actualPercepts.add(speedLimitLiteral);
        addPercept(speedLimitLiteral);
        removePerceptsByUnif(signTypeToLiteral(Sign.SignType.STOP));
        RoadElements.getNextStopSign(movement.getNextStreetElements())
                .ifPresent(s -> {
                    var signLiteral = signToLiteral(s);
                    actualPercepts.add(signLiteral);
                    addPercept(signLiteral);
                });
        RoadElements.getNextTrafficLight(movement.getNextStreetElements())
                .ifPresent(s -> {
                    var trafficLightLiteral = trafficLightToLiteral(s);
                    actualPercepts.add(trafficLightLiteral);
                    addPercept(trafficLightLiteral);
                });


        otherCar.ifPresent(value -> {
            var otherCarLiteral = otherCarToLiteral(value);
            actualPercepts.add(otherCarLiteral);
            addPercept(otherCarLiteral);
        });

        var info = actualPercepts.stream().map(Literal::toString).reduce("", (a, b) -> a + b + "\n");
        gui.changeInfo(info);

        logger.info(String.format("Current speed: %.2f", car.getSpeed()));

    }

    /*
    @Override
    public Collection<Literal> getPercepts(String agName) {
        final Point pos = movement.getCurrentPosition();
        logger.info(String.format("Current position: (%.2f, %.2f)", pos.getX(), pos.getY()));
        return Collections.singletonList(
                Literal.parseLiteral(String.format("position(%.2f, %.2f)", pos.getX(), pos.getY()))
        );
    }*/

    //private static final double FAILURE_PROBABILITY = 0.2;

    @Override
    public boolean executeAction(final String ag, final Structure action) {
        logger.info(ag + " is doing: " + action);
        try {
            double mySpeed = car.getSpeed();
            switch (action.getFunctor()) {
                case "accelerate":
                    double speedIncrease = 10;//((NumberTerm) action.getTerm(0)).solve();
                    mySpeed += speedIncrease;
                    car.setSpeed(mySpeed);
                    break;

                case "brake":
                    double speedDecrease = 5;//((NumberTerm) action.getTerm(0)).solve();
                    mySpeed -= speedDecrease;
                    if (mySpeed < 0) mySpeed = 0;
                    car.setSpeed(mySpeed);
                    break;

                case "keep_speed":
                    // cruise control: do nothing
                    break;

                case "do_nothing":
                    // natural drag/friction
                    mySpeed -= 0.5;
                    if (mySpeed < 0) mySpeed = 0;
                    car.setSpeed(mySpeed);
                    break;
                case "move":
                    double distance = mySpeed * 0.5; // simple discrete timestep
                    movement.move(distance);
                    break;
                case "passedStop":
                    logger.info(ag + " is passed: " + action);
                    RoadElements.getNextStopSign(movement.getNextStreetElements())
                            .ifPresent(Sign::useSignal);
                    new Thread(
                            () -> {
                                try {
                                    Thread.sleep(2000);
                                    addPercept(Literal.parseLiteral("restart"));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                default:
                    logger.warning("Unknown action " + action);
                    return false;
            }

            // simulate movement
            //myPos += mySpeed * 0.5; // simple discrete timestep

            // update percepts for next cycle
            updatePercepts();
        } catch (Exception e) {
            logger.warning("Error executing action: " + e.getMessage());
            //e.printStackTrace();
        }
        return true;
    }
}
