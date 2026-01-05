package repository.dto.path;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PathDto {

    private final String id;
    private final List<PathSegmentDto> segments;

    @JsonCreator
    public PathDto(
            @JsonProperty("id") String id,
            @JsonProperty("segments") List<PathSegmentDto> segments
    ) {
        this.id = id;
        this.segments = segments;
    }

    public String getId() {
        return id;
    }

    public List<PathSegmentDto> getSegments() {
        return segments;
    }
}
