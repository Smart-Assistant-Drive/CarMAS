package repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MqttRepository {
    private final ObjectMapper mapper;
    private final MqttClient client;
    private final List<MqttEventListener> listeners = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Logger logger;

    public MqttRepository(Logger logger) throws MqttException {
        this("tcp://127.0.0.1:1883", logger);
    }

    public MqttRepository(String brokerUrl, Logger logger) throws MqttException {
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.client = new MqttClient(brokerUrl, "traffic-light-client");
        this.logger = logger;
        this.logger.log("Connecting to broker at " + brokerUrl);
        connectAndSubscribe();
    }

    private void connectAndSubscribe() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setKeepAliveInterval(30);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("MQTT lost: " + cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                MqttEvent event = new MqttEvent(topic, payload);
                for (MqttEventListener listener : listeners) {
                    executor.submit(() -> listener.onEvent(event));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // No-op
            }
        });

        client.connect(options);
        client.subscribe("semaphore/+/change", 1);
    }

    public void connectToTopic(String topic) throws MqttException {
        client.subscribe(topic, 1);
    }

    public void addEventListener(MqttEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(MqttEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(MqttEvent event) {
        try {
            MqttMessage mqttMessage = new MqttMessage(event.getMessage().getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(1);
            client.publish(event.getTopic(), mqttMessage);
        } catch (MqttException e) {
            logger.log("Failed to publish message to topic " + event.getTopic() + ": " + e.getMessage());
        }
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public interface MqttEventListener {
        void onEvent(MqttEvent event);
    }

    public interface Logger {
        void log(String message);
    }
}
