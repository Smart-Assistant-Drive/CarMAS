package repository.dto.car;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarUpdateMessage {
    private final String idCar;
    private final double currentSpeed;
    private final int lane;
    private final String state;
    private final double positionX;
    private final double positionY;
    private final int indexP;
    private final double dPointX;
    private final double dPointY;

    public CarUpdateMessage(
            @JsonProperty("idCar") String idCar,
            @JsonProperty("currentSpeed") double currentSpeed,
            @JsonProperty("lane") int lane,
            @JsonProperty("state") String state,
            @JsonProperty("positionX") double positionX,
            @JsonProperty("positionY") double positionY,
            @JsonProperty("indexP") int indexP,
            @JsonProperty("dPointX") double dPointX,
            @JsonProperty("dPointY") double dPointY
    ) {
        this.idCar = idCar;
        this.currentSpeed = currentSpeed;
        this.lane = lane;
        this.state = state;
        this.positionX = positionX;
        this.positionY = positionY;
        this.indexP = indexP;
        this.dPointX = dPointX;
        this.dPointY = dPointY;
    }

    public String getIdCar() { return idCar; }
    public double getCurrentSpeed() { return currentSpeed; }
    public int getLane() { return lane; }
    public String getState() { return state; }
    public double getPositionX() { return positionX; }
    public double getPositionY() { return positionY; }
    public int getIndexP() { return indexP; }
    @JsonProperty("dPointX")
    public double getDPointX() { return dPointX; }
    @JsonProperty("dPointY")
    public double getDPointY() { return dPointY; }
}
