package repository.dto.semaphore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficLightMessage {
    private final String color;

    public TrafficLightMessage(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
