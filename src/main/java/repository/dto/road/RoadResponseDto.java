package repository.dto.road;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

/**
 *
 */
public class RoadResponseDto extends RepresentationModel<RoadResponseDto> {

    private final String roadId;
    private final String roadNumber;
    private final String roadName;
    private final String category;

    /**
     *
     */
    @JsonCreator
    public RoadResponseDto(
            @JsonProperty("roadId") String roadId,
            @JsonProperty("roadNumber") String roadNumber,
            @JsonProperty("roadName") String roadName,
            @JsonProperty("category") String category
    ) {
        this.roadId = roadId;
        this.roadNumber = roadNumber;
        this.roadName = roadName;
        this.category = category;
    }

    public String getRoadId() {
        return roadId;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public String getRoadName() {
        return roadName;
    }

    public String getCategory() {
        return category;
    }
}