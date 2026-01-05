package repository.dto.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CoordinateDto {

    private final double x;
    private final double y;

    @JsonCreator
    public CoordinateDto(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y
    ) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
