package env;

import gui.GUI;
import gui.GUIEventInterface;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import model.Car;
import model.Movement;
import model.OtherCar;
import model.RoadsElementsVision;
import model.math.Point;
import model.path.Path;
import model.roadElement.RoadElements;
import model.roadElement.Sign;
import model.roadElement.SpeedLimitSign;
import org.eclipse.paho.client.mqttv3.MqttException;
import repository.MovementListenerMqtt;
import repository.MqttRepository;
import repository.RemoteRepository;
import repository.RemoteStream;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static model.LiteralConverter.*;
import static utils.LicensePlateGenerator.randomPlate;

public class CarEnvironment extends Environment {

    private static final Logger logger =
            Logger.getLogger(CarEnvironment.class.getName());

    private Movement movement;
    private RoadsElementsVision roadsElementsVision;

    private final String plate =  randomPlate();
    private final Car car = new Car(new Point(0, 0), 0, plate);

    private GUI gui;
    //private String licenseNumber = "";

    private final RemoteRepository repository = new RemoteRepository();
    private final RemoteStream remoteStream = new RemoteStream();
    private MqttRepository mqttRepository = null;

    /* =========================
       ENV INITIALIZATION
       ========================= */

    @Override
    public void init(String[] args) {
        logger.info("Initializing environment...");
        try {
            mqttRepository = new MqttRepository(logger::warning);
        } catch (MqttException e) {
            logger.severe("Failed to connect to MQTT broker: " + e.getMessage());
            throw new RuntimeException(e);
        }

        initGUI();
        initModel();
        initPercepts();

        logger.info("Environment ready.");
    }

    private void initGUI() {
        //CarEnvironment env = this;

        gui = new GUI(new GUIEventInterface() {

            @Override
            public void onInsertLicense(String license) {
                //env.licenseNumber = license;
            }

            @Override
            public void onStartCar() {
                removePercept(Literal.parseLiteral("car_stopped"));
                addPercept(Literal.parseLiteral("car_started"));
            }

            @Override
            public void onStopCar() {
                removePercept(Literal.parseLiteral("car_started"));
                addPercept(Literal.parseLiteral("car_stopped"));
            }

            @Override
            public void onSpeedChange(double newSpeed) {
                car.setSpeed(newSpeed);
                updatePercepts();
            }

            @Override
            public void onToggleAutonomousModeChange(boolean auto) {
                if (auto) {
                    removePercept(Literal.parseLiteral("driver_mode"));
                    addPercept(Literal.parseLiteral("autonomous_mode"));
                } else {
                    removePercept(Literal.parseLiteral("autonomous_mode"));
                    addPercept(Literal.parseLiteral("driver_mode"));
                }
            }
        });

        SwingUtilities.invokeLater(() -> gui.setVisible(true));
    }

    private void initModel() {
        MapFactory factory = new MapFactory(repository);
        try{
            factory.buildMap();
        } catch (RuntimeException e){
            logger.severe("Failed to build map: " + e.getMessage());
            throw e;
        }
        Path path = factory.getPath();
        car.setPosition(
                path.getSegments().getFirst().getStart()
        );
        movement = new Movement(car, path);
        roadsElementsVision = new RoadsElementsVision(factory.getRoadElements());
        movement.addListener(roadsElementsVision);
        factory.getTrafficLights().forEach(
                t ->
                        remoteStream.trafficLightStateStream(
                                "",
                                mqttRepository,
                                t::setState
                        )
        );
        try {
            remoteStream.nextCarStream(
                    mqttRepository,
                    car.getPlate(),
                    this::updateOtherCarPercept
            );
        }
        catch (MqttException e){
            logger.severe("Failed to initialize other car stream: " + e.getMessage());
            throw new RuntimeException(e);
        }
        MovementListenerMqtt movementListenerMqtt =
                new MovementListenerMqtt(remoteStream, mqttRepository);
        movement.addListener(movementListenerMqtt);
    }

    private void updateOtherCarPercept(OtherCar car) {
        removePerceptsByUnif(Literal.parseLiteral(OTHER_CAR_BASE));
        addPercept(otherCarToLiteral(car));
    }

    private void initPercepts() {
        addPercept(Literal.parseLiteral("car_stopped"));
        addPercept(currentSpeedToLiteral(car.getSpeed()));
        addPercept(positionToLiteral(car.getPosition()));
        addSpeedLimitPercept();
    }

    /* =========================
       PERCEPTS UPDATE
       ========================= */

    private void updatePercepts() {
        updatePositionPercept();
        updateSpeedPercept();
        updateSpeedLimitPercept();
        updateSignalsPercepts();
        updateGUIInfo();
    }

    private void updatePositionPercept() {
        removePerceptsByUnif(Literal.parseLiteral(POSITION_BASE));
        addPercept(positionToLiteral(car.getPosition()));
    }

    private void updateSpeedPercept() {
        removePerceptsByUnif(Literal.parseLiteral(CURRENT_SPEED_BASE));
        addPercept(currentSpeedToLiteral(car.getSpeed()));
        gui.changeSpeed(car.getSpeed());
    }

    private void updateSpeedLimitPercept() {
        removePerceptsByUnif(Literal.parseLiteral(SPEED_LIMIT_BASE));
        addSpeedLimitPercept();
    }

    private void addSpeedLimitPercept() {
        int speedLimit =
                RoadElements.getLastSpeedLimitSign(
                                roadsElementsVision.getPassedRoadElements()
                        ).map(SpeedLimitSign::getSpeedLimit)
                        .orElse(60);

        addPercept(speedLimitToLiteral(speedLimit));
    }

    private void updateSignalsPercepts() {

        removePerceptsByUnif(signTypeToLiteral(Sign.SignType.STOP));

        RoadElements.getNextStopSign(
                roadsElementsVision.getNextRoadElements()
        ).ifPresent(s -> addPercept(signToLiteral(s)));

        RoadElements.getNextTrafficLight(
                roadsElementsVision.getNextRoadElements()
        ).ifPresent(t -> addPercept(trafficLightToLiteral(t)));
    }

    private void updateGUIInfo() {
        String info =
                Stream.concat(
                                roadsElementsVision.getNextRoadElements().stream(),
                                roadsElementsVision.getPassedRoadElements().stream()
                        )
                        .map(Object::toString)
                        .reduce("", (a, b) -> a + b + "\n");

        gui.changeInfo(info);
    }

    /* =========================
       ACTION EXECUTION
       ========================= */

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " executes " + action);

        double speed = car.getSpeed();

        switch (action.getFunctor()) {

            case "accelerate":
                car.setSpeed(speed + 10);
                break;

            case "brake":
                car.setSpeed(Math.max(0, speed - 5));
                break;

            case "keep_speed":
                break;

            case "do_nothing":
                car.setSpeed(Math.max(0, speed - 0.5));
                break;

            case "move":
                movement.move(car.getSpeed() * 0.5);
                //roadsElementsVision.update(movement.getPosition());
                break;

            case "passedStop":
                handlePassedStop();
                break;

            default:
                logger.warning("Unknown action: " + action);
                return false;
        }

        updatePercepts();
        return true;
    }

    /* =========================
       STOP HANDLING
       ========================= */

    private void handlePassedStop() {

        RoadElements.getNextStopSign(
                roadsElementsVision.getNextRoadElements()
        ).ifPresent(Sign::useSignal);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addPercept(Literal.parseLiteral("restart"));
                timer.cancel();
            }
        }, 2000);
    }
}