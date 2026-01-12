package env;

import com.fasterxml.jackson.core.JsonProcessingException;
import env.car_action.*;
import gui.GUI;
import gui.GUIEventInterface;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import model.*;
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
import java.util.*;
import java.util.Timer;
import java.util.logging.Logger;

import static model.LiteralConverter.*;
import static utils.LicensePlateGenerator.randomPlate;

public class CarEnvironment extends Environment {

    private static final Logger logger =
            Logger.getLogger(CarEnvironment.class.getName());

    private MovementEngine movementEngine;
    private RoadsElementsVision roadsElementsVision;

    private final String plate = randomPlate();
    private final Car car = new Car(new Point(0, 0), 0, plate);

    private GUI gui;
    //private String licenseNumber = "";

    private final RemoteRepository repository = new RemoteRepository();
    private final RemoteStream remoteStream = new RemoteStream();
    private MqttRepository mqttRepository = null;

    private final env.Timer envTimer = new env.Timer();
    private final Scaler scaler = new Scaler(1);
    private Optional<OtherCar> otherCar = Optional.empty();

    private final int defaultSpeedLimit = 50;

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

        registerActions();
        logger.info("Environment ready.");
    }

    private void initGUI() {
        //CarEnvironment env = this;

        gui = new GUI(plate, new GUIEventInterface() {

            @Override
            public void onInsertLicense(String license) {
                //env.licenseNumber = license;
            }

            @Override
            public void onStartCar() {
                removePercept(Literal.parseLiteral("car_stopped"));
                addPercept(Literal.parseLiteral("car_started"));
                envTimer.reset();
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
        try {
            factory.buildMap();
        } catch (RuntimeException e) {
            logger.severe("Failed to build map: " + e.getMessage());
            throw e;
        }
        Path path = factory.getPath();
        car.setPosition(
                path.getSegments().getFirst().getStart()
        );
        movementEngine = new MovementEngine(car, path);
        roadsElementsVision = new RoadsElementsVision(factory.getRoadElements());
        movementEngine.addListener(roadsElementsVision);
        factory.getTrafficLights().forEach(
                t ->
                {
                    try {
                        remoteStream.trafficLightStateStream(
                                t,
                                mqttRepository,
                                trafficLight -> updateSignalsPercepts()
                        );
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        try {
            remoteStream.nextCarStream(
                    mqttRepository,
                    car.getPlate(),
                    this::updateOtherCarPercept
            );
        } catch (MqttException e) {
            logger.severe("Failed to initialize other car stream: " + e.getMessage());
            throw new RuntimeException(e);
        }
        MovementListenerMqtt movementListenerMqtt =
                new MovementListenerMqtt(remoteStream, mqttRepository);
        movementEngine.addListener(movementListenerMqtt);
        try {
            remoteStream.sendCarEnterRoad(
                    mqttRepository,
                    new CarUpdate(
                            car,
                            movementEngine.getCurrentFlow(),
                            movementEngine.getCurrentFlowSegmentIndex(),
                            movementEngine.getDistanceFromSegmentStart()
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error sending car ENTER road", e);
        }
        roadsElementsVision.onRoadChanged(
                null,
                movementEngine.getCurrentFlow(),
                path.getSegments().getFirst(),
                movementEngine.getCurrentRoadPosition(),
                car,
                0,
                movementEngine.getDistanceFromSegmentStart()
        );
    }

    private void updateOtherCarPercept(Optional<OtherCar> car) {
        removePerceptsByUnif(Literal.parseLiteral(OTHER_CAR_BASE));
        if(car.isEmpty()) {
            otherCar = Optional.empty();
            updatePercepts();
        }else {
            addPercept(otherCarToLiteral(car.get(), scaler));
            otherCar = car;
            updatePercepts();
        }
    }

    private void initPercepts() {
        addPercept(Literal.parseLiteral("car_stopped"));
        addPercept(currentSpeedToLiteral(car.getSpeed()));
        addPercept(positionToLiteral(car.getPosition()));
        addPercept(destinationToLiteral(movementEngine.getDestination()));
        addSpeedLimitPercept();
    }

    /* =========================
       PERCEPTS UPDATE
       ========================= */

    private void updatePercepts() {
        updatePositionPercept();
        updateSpeedPercept();
        var speedLimit = updateSpeedLimitPercept();
        var signals = updateSignalsPercepts();
        var otherCarString = "";
        if (otherCar.isPresent()) {
            otherCarString = otherCarToLiteral(otherCar.get(), scaler).toString();
        }
        updateGUIInfo(speedLimit + signals + otherCarString);
    }

    private void updatePositionPercept() {
        removePerceptsByUnif(Literal.parseLiteral(POSITION_BASE));
        addPercept(positionToLiteral(car.getPosition()));
    }

    private void updateSpeedPercept() {
        double scaleSpeed = scaler.descaleValue(car.getSpeed());
        removePerceptsByUnif(Literal.parseLiteral(CURRENT_SPEED_BASE));
        addPercept(currentSpeedToLiteral(scaleSpeed));
        gui.changeSpeed(scaleSpeed);
    }

    private String updateSpeedLimitPercept() {
        removePerceptsByUnif(Literal.parseLiteral(SPEED_LIMIT_BASE));
        return addSpeedLimitPercept();
    }

    private String addSpeedLimitPercept() {
        int speedLimit =
                RoadElements.getLastSpeedLimitSign(
                                roadsElementsVision.getPassedRoadElements()
                        ).map(SpeedLimitSign::getSpeedLimit)
                        .orElse(defaultSpeedLimit);
        var literal = speedLimitToLiteral(speedLimit);
        addPercept(literal);
        return literal.toString() + "\n";
    }

    private String updateSignalsPercepts() {

        removePerceptsByUnif(signTypeToLiteral(Sign.SignType.STOP));

        var literalStop = RoadElements.getNextStopSign(
                roadsElementsVision.getNextRoadElements()
        ).map(LiteralConverter::signToLiteral);
        literalStop.ifPresent(this::addPercept);

        removePerceptsByUnif(Literal.parseLiteral(TRAFFIC_LIGHT_BASE));
        var literalTrafficLight =
                RoadElements.getNextTrafficLight(
                        roadsElementsVision.getNextRoadElements()
                ).map(LiteralConverter::trafficLightToLiteral);
        literalTrafficLight.ifPresent(this::addPercept);
        return literalStop
                    .map(Object::toString)
                    .map(s -> s+"\n")
                    .orElse("")
                + literalTrafficLight
                        .map(Object::toString)
                        .map(s -> s+"\n")
                        .orElse("");
    }

    private void updateGUIInfo(String info) {
        gui.changeInfo(info);
    }

    /* =========================
       ACTION EXECUTION
       ========================= */

    private final Map<String, CarAction> actions = new HashMap<>();

    private void registerActions() {
        actions.put("accelerate", new AccelerateAction());
        actions.put("brake", new BrakeAction());
        actions.put("keep_speed", new KeepSpeedAction());
        actions.put("do_nothing", new DoNothingAction());
        actions.put("move", new MoveAction(envTimer, movementEngine));
        actions.put("passedStop", new PassedStopAction(this::handlePassedStop));
    }

    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " executes " + action);

        CarAction carAction = actions.get(action.getFunctor());

        if (carAction == null) {
            logger.warning("Unknown action: " + action.getFunctor());
            return false;
        }

        boolean result = carAction.execute(car);

        updatePercepts();
        return result;
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