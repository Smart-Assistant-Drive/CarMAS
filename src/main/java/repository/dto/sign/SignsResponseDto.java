package repository.dto.sign;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignsResponseDto {
    private final List<SignResponseDto> signs;

    @JsonCreator
    public SignsResponseDto(@JsonProperty("signs") List<SignResponseDto> signs) {
        this.signs = signs;
    }

    public List<SignResponseDto> getSigns() {
        return signs;
    }
}
