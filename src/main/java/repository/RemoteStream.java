package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Car;
import model.roadElement.TrafficLight;
import repository.dto.semaphore.TrafficLightMessage;
import java.util.Objects;

public class RemoteStream {
    private static String trafficLightTopic(String id) {
        return "semaphore/" + id + "/change";
    }
    //private static final String CAR_UPDATE_TOPIC = "trafficdt-digital-cars-digital-adapter/cars/carUpdate";

    public interface TrafficLightStateListener {
        void onTrafficLightState(TrafficLight.State state);
    }

    public interface CarListener {
        void onCar(Car car);
    }

    public void trafficLightStateStream(String id, MqttRepository mqttRepository, TrafficLightStateListener listener) {
        mqttRepository.addEventListener(event -> {
            if (Objects.equals(event.getTopic(), trafficLightTopic(id))) {
                try {
                    ObjectMapper mapper = mqttRepository.getMapper();
                    TrafficLightMessage msg = mapper.readValue(event.getMessage(), TrafficLightMessage.class);
                    TrafficLight.State state;
                    switch (msg.getColor().toLowerCase()) {
                        case "green":
                            state = TrafficLight.State.GREEN;
                            break;
                        case "yellow":
                            state = TrafficLight.State.YELLOW;
                            break;
                        default:
                            state = TrafficLight.State.RED;
                    }
                    listener.onTrafficLightState(state);
                } catch (Exception ignored) {}
            }
        });
    }

    public void sendMessage(String topic, String message, MqttRepository mqttRepository) {
        mqttRepository.publish(new MqttEvent(topic, message));
    }

    /*public void carsStream(MqttRepository mqttRepository, CarListener listener) {
        mqttRepository.addEventListener(event -> {
            if (Objects.equals(event.getTopic(), CAR_UPDATE_TOPIC)) {
                try {
                    ObjectMapper mapper = mqttRepository.getMapper();
                    CarUpdateMessage msg = mapper.readValue(event.getPayload(), CarUpdateMessage.class);
                    Car car = new Car(
                        msg.getIdCar(),
                        new Point(msg.getPositionX(), msg.getPositionY()),
                        msg.getCurrentSpeed(),
                        new Vector(msg.getDPointX(), msg.getDPointY()).normalize(),
                        msg.getIndexLane()
                    );
                    listener.onCar(car);
                } catch (Exception ignored) {}
            }
        });
    }*/
}
