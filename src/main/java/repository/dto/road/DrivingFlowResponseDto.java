package repository.dto.road;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import repository.dto.util.CoordinateDto;

import java.util.List;

public class DrivingFlowResponseDto extends RepresentationModel<DrivingFlowResponseDto> {
    private final String flowId;
    private final String roadId;
    private final int idDirection;
    private final int numOfLanes;
    private final List<CoordinateDto> roadCoordinates;

    @JsonCreator
    public DrivingFlowResponseDto(
            @JsonProperty("flowId") String flowId,
            @JsonProperty("roadId") String roadId,
            @JsonProperty("idDirection") int idDirection,
            @JsonProperty("numOfLanes") int numOfLanes,
            @JsonProperty("roadCoordinates") List<CoordinateDto> roadCoordinates
    ) {
        this.flowId = flowId;
        this.roadId = roadId;
        this.idDirection = idDirection;
        this.numOfLanes = numOfLanes;
        this.roadCoordinates = roadCoordinates;
    }

    public String getFlowId() {
        return flowId;
    }

    public String getRoadId() {
        return roadId;
    }

    public int getIdDirection() {
        return idDirection;
    }

    public int getNumOfLanes() {
        return numOfLanes;
    }

    public List<CoordinateDto> getRoadCoordinates() {
        return roadCoordinates;
    }
}
