package repository.dto.semaphore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficLightMessage {
    private final String color;

    public TrafficLightMessage(
            @JsonProperty("color")
            String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
