package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import repository.dto.path.PathDto;
import repository.dto.road.DrivingFlowResponseDto;
import repository.dto.road.RoadResponseDto;
import repository.dto.semaphore.SemaphoreDto;
import repository.dto.sign.SignsResponseDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class RemoteRepository {
    private final String brokerAdministration;
    private final String brokerPublic;
    private final ObjectMapper mapper;
    private final HttpClient client;

    public RemoteRepository() {
        this("http://localhost:8087", "http://localhost:8086");
    }

    public RemoteRepository(String brokerAdministration, String brokerPublic) {
        this.brokerAdministration = brokerAdministration;
        this.brokerPublic = brokerPublic;
        this.mapper = new ObjectMapper().registerModule(new Jackson2HalModule());
        this.client = HttpClient.newHttpClient();
    }

    public PathDto getPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerPublic + "/path/next"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), PathDto.class);
    }

    public List<RoadResponseDto> getRoads() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerAdministration + "/roads"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<RoadResponseDto>>() {});
    }

    public RoadResponseDto getRoad(String roadId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerAdministration + "/road/" + roadId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), RoadResponseDto.class);
    }

    public List<DrivingFlowResponseDto> getFlows(String roadId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerAdministration + "/flows/" + roadId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<DrivingFlowResponseDto>>() {});
    }

    public List<SemaphoreDto> getTrafficLights(String roadId, String direction) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerAdministration + "/semaphores/" + roadId + "/" + direction))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<SemaphoreDto>>() {});
    }

    public SignsResponseDto getSigns(String roadId, String direction) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(brokerAdministration + "/signs/" + roadId + "/" + direction))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), SignsResponseDto.class);
    }
}
