package repository.dto.semaphore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import repository.dto.util.CoordinateDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SemaphoreDto {
    private String link;
    private String road;
    private int direction;
    private CoordinateDto positionDto;
    private String status;
    private String id;
    private int idIndex;

    public static final String READY = "ready";
    public static final String OFFLINE = "offline";

    public SemaphoreDto(
            @JsonProperty("link") String link,
            @JsonProperty("road") String road,
            @JsonProperty("direction") int direction,
            @JsonProperty("position") CoordinateDto positionDto,
            @JsonProperty("status") String status,
            @JsonProperty("id") String id,
            @JsonProperty("idIndex") int idIndex
    ) {
        this.link = link;
        this.road = road;
        this.direction = direction;
        this.positionDto = positionDto;
        this.status = status;
        this.id = id;
        this.idIndex = idIndex;
    }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getRoad() { return road; }
    public void setRoad(String road) { this.road = road; }

    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }

    public CoordinateDto getPositionDto() { return positionDto; }
    public void setPositionDto(CoordinateDto positionDto) { this.positionDto = positionDto; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getIdIndex() { return idIndex; }
    public void setIdIndex(int idIndex) { this.idIndex = idIndex; }
}
