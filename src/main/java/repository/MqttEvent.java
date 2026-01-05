package repository;

public class MqttEvent {

    private final String topic;
    private final String message;

    public MqttEvent(
            String topic,
            String message
    ) {
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }
}
