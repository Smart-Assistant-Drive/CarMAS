package repository.dto.car;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherCarMessage {
    @JsonProperty("idCar")
    private String idCar;

    @JsonProperty("idNextCar")
    private String idNextCar;

    @JsonProperty("distance")
    private double distance;

    @JsonProperty("speed")
    private int speed;

    public OtherCarMessage(
        @JsonProperty("idCar") String idCar,
        @JsonProperty("idNextCar") String idNextCar,
        @JsonProperty("distance") double distance,
        @JsonProperty("speed") int speed
    ) {
        this.idCar = idCar;
        this.idNextCar = idNextCar;
        this.distance = distance;
        this.speed = speed;
    }

    public String getIdCar() { return idCar; }
    public String getIdNextCar() { return idNextCar; }
    public double getDistance() { return distance; }
    public int getSpeed() { return speed; }
}
