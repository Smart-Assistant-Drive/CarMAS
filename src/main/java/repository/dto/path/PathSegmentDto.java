package repository.dto.path;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import repository.dto.util.CoordinateDto;

public class PathSegmentDto {

    private final CoordinateDto from;
    private final CoordinateDto to;
    private final String roadId;

    @JsonCreator
    public PathSegmentDto(
            @JsonProperty("from") CoordinateDto from,
            @JsonProperty("to") CoordinateDto to,
            @JsonProperty("roadId") String roadId
    ) {
        this.from = from;
        this.to = to;
        this.roadId = roadId;
    }

    public CoordinateDto getFrom() {
        return from;
    }

    public CoordinateDto getTo() {
        return to;
    }

    public String getRoadId() {
        return roadId;
    }
}
