package repository.dto.sign;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignResponseDto {
    private final String type;
    private final String category;
    private final String idRoad;
    private final int direction;
    private final double latitude;
    private final double longitude;
    private final String lanes;
    private final Integer speedLimit;
    private final String unit;

    @JsonCreator
    public SignResponseDto(
            @JsonProperty("type") String type,
            @JsonProperty("category") String category,
            @JsonProperty("idRoad") String idRoad,
            @JsonProperty("direction") int direction,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("lanes") String lanes,
            @JsonProperty("speedLimit") Integer speedLimit,
            @JsonProperty("unit") String unit
    ) {
        this.type = type;
        this.category = category;
        this.idRoad = idRoad;
        this.direction = direction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lanes = lanes;
        this.speedLimit = speedLimit;
        this.unit = unit;
    }

    public String getType() { return type; }
    public String getCategory() { return category; }
    public String getIdRoad() { return idRoad; }
    public int getDirection() { return direction; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLanes() { return lanes; }
    public Integer getSpeedLimit() { return speedLimit; }
    public String getUnit() { return unit; }
}
