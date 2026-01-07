package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import model.Car;
import model.CarUpdate;
import model.MovementListener;
import model.RoadPosition;
import model.math.Vector;
import model.path.Flow;
import model.path.PathSegment;

public class MovementListenerMqtt implements MovementListener {

    private final RemoteStream remoteStream;
    private final MqttRepository mqttRepository;

    public MovementListenerMqtt(
            RemoteStream remoteStream,
            MqttRepository mqttRepository
    ) {
        this.remoteStream = remoteStream;
        this.mqttRepository = mqttRepository;
    }

    @Override
    public void onRoadChanged(
            Flow previousFlow,
            Flow newFlow,
            PathSegment newSegment,
            RoadPosition roadPosition,
            Car car,
            int indexFlow,
            Vector distance
    ) {

        // ENTER nel nuovo flow (presenza anticipata)
        if (newFlow != null) {
            try {
                remoteStream.sendCarEnterRoad(
                        mqttRepository,
                        new CarUpdate(
                                car,
                                newFlow,
                                indexFlow,
                                distance
                        )
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error sending car ENTER road", e);
            }
        }

        // EXIT dal flow precedente (uscita ritardata)
        if (previousFlow != null) {
            remoteStream.sendCarExitRoad(
                    mqttRepository,
                    new CarUpdate(
                            car,
                            previousFlow,
                            indexFlow,
                            distance
                    )
            );
        }
    }

    @Override
    public void onMovementUpdated(
            Flow flow,
            Car car,
            RoadPosition carPos,
            int indexFlow,
            Vector distance
    ) {
        try {
            remoteStream.sendCarUpdate(
                    mqttRepository,
                    new CarUpdate(
                            car,
                            flow,
                            indexFlow,
                            distance
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error sending car UPDATE", e);
        }
    }
}