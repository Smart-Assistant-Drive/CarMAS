package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CarUpdate;
import model.OtherCar;
import model.path.Flow;
import model.roadElement.TrafficLight;
import org.eclipse.paho.client.mqttv3.MqttException;
import repository.dto.car.CarUpdateMessage;
import repository.dto.car.OtherCarMessage;
import repository.dto.semaphore.TrafficLightMessage;
import java.util.Objects;

import static repository.Mapper.mapCarUpdateToCarUpdateMessage;
import static repository.Mapper.mapOtherCarDtoToOtherCar;

public class RemoteStream {
    private String trafficLightTopic(String id) {
        return "semaphore/" + id + "/change";
    }
    private String carUpdateTopic(String plate) {
        return "trafficdt-digital-cars/+/cars/"+plate+"/distanceFromNext";
    }

    private String trafficTDId(Flow flow) {
        return "trafficdt-"+flow.getRoad()+"-"+flow.getDirection();
    }

    private String EnterRoadTopic(Flow flow) {
        return "trafficdt-physical-"+trafficTDId(flow)+"/carEntered";
    }

    private String UpdateRoadTopic(Flow flow) {
        return "trafficdt-physical-"+trafficTDId(flow)+"/carUpdate";
    }

    private String ExitRoadTopic(Flow flow) {
        return "trafficdt-physical-"+trafficTDId(flow)+"/carExited";
    }

    public interface CarListener {
        void onOtherCar(OtherCar car);
    }

    public interface TrafficLightListener {
        void onTrafficLightChange(TrafficLight trafficLight);
    }

    public void trafficLightStateStream(TrafficLight trafficLight, MqttRepository mqttRepository, TrafficLightListener listener) throws MqttException {
        mqttRepository.addEventListener(event -> {
            if (Objects.equals(event.getTopic(), trafficLightTopic(trafficLight.getId()))) {
                try {
                    ObjectMapper mapper = mqttRepository.getMapper();
                    TrafficLightMessage msg = mapper.readValue(event.getMessage(), TrafficLightMessage.class);
                    TrafficLight.State state = switch (msg.getColor().toLowerCase()) {
                        case "green" -> TrafficLight.State.GREEN;
                        case "yellow" -> TrafficLight.State.YELLOW;
                        default -> TrafficLight.State.RED;
                    };
                    trafficLight.setState(state);
                    listener.onTrafficLightChange(trafficLight);
                } catch (Exception ignored) {}
            }
        });
    }

    public void nextCarStream(MqttRepository mqttRepository, String plate, CarListener listener) throws MqttException {
        mqttRepository.connectToTopic(carUpdateTopic(plate));
        mqttRepository.addEventListener(event -> {
            if (event.getTopic().matches(carUpdateTopic(plate).replace("+", "[^/]+"))) {
                try {
                    ObjectMapper mapper = mqttRepository.getMapper();
                    OtherCarMessage otherCar = mapper.readValue(event.getMessage(), OtherCarMessage.class);
                    if(!Objects.equals(otherCar.getIdNextCar(), plate)) {
                        listener.onOtherCar(mapOtherCarDtoToOtherCar(otherCar));
                    }
                } catch (Exception ignored) {}
            }
        });
    }

    private void sendMessage(MqttRepository mqttRepository, MqttEvent event) {
        mqttRepository.publish(event);
    }

    public void sendCarEnterRoad(MqttRepository mqttRepository, CarUpdate update) throws JsonProcessingException {
        CarUpdateMessage msg = mapCarUpdateToCarUpdateMessage(update);
        ObjectMapper mapper = mqttRepository.getMapper();
        MqttEvent event = new MqttEvent(
                EnterRoadTopic(update.getFlow()),
                mapper.writeValueAsString(msg)
        );
        sendMessage(mqttRepository, event);
    }

    public void sendCarUpdate(MqttRepository mqttRepository, CarUpdate update) throws JsonProcessingException {
        CarUpdateMessage msg = mapCarUpdateToCarUpdateMessage(update);
        ObjectMapper mapper = mqttRepository.getMapper();
        MqttEvent event = new MqttEvent(
                UpdateRoadTopic(update.getFlow()),
                mapper.writeValueAsString(msg)
        );
        sendMessage(mqttRepository, event);
    }

    public void sendCarExitRoad(MqttRepository mqttRepository, CarUpdate update) {
        MqttEvent event = new MqttEvent(
                ExitRoadTopic(update.getFlow()),
                update.getCar().getPlate()
        );
        sendMessage(mqttRepository, event);
    }
}
