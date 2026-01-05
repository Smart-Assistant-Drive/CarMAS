package env;

import model.math.Point;
import model.path.Path;
import model.path.Flow;
import model.path.PathSegment;
import model.roadElement.RoadElement;
import model.roadElement.Sign;
import model.roadElement.TrafficLight;
import repository.Mapper;
import repository.RemoteRepository;
import repository.dto.path.PathDto;
import repository.dto.road.DrivingFlowResponseDto;
import repository.dto.semaphore.SemaphoreDto;
import repository.dto.sign.SignsResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFactory {

    private final RemoteRepository remoteRepository;
    private Path path = null;
    private final List<Flow> flows = new ArrayList<>();
    private final List<TrafficLight> trafficLights = new ArrayList<>();
    private final List<Sign> signs = new ArrayList<>();

    public MapFactory(RemoteRepository remoteRepository) {
        this.remoteRepository = remoteRepository;
    }

    public void buildMap() throws RuntimeException {
        try {
            PathDto pathdto = remoteRepository.getPath();
            List<PathSegment> pathSegments = new java.util.ArrayList<>();
            for (var segment : pathdto.getSegments()) {
                //RoadResponseDto roadDto = remoteRepository.getRoad(segment.getRoadId());
                DrivingFlowResponseDto flowDto =
                        remoteRepository
                                .getFlows(segment.getRoadId())
                                .stream()
                                .filter(f -> f.getIdDirection() == segment.getDirection())
                                .findFirst()
                                .orElseThrow();
                Flow flow = new Flow(
                        flowDto.getRoadId(),
                        flowDto.getIdDirection(),
                        flowDto.getRoadCoordinates()
                        .stream()
                        .map(Mapper::mapCoordinateDtoToPoint)
                        .toArray(Point[]::new)
                );
                flows.add(flow);
                PathSegment pathSegment = Mapper.mapPathSegmentDtoToPathSegment(segment, flow);
                pathSegments.add(pathSegment);
                SignsResponseDto signsDto = remoteRepository.getSigns(flowDto.getRoadId(), flowDto.getFlowId());
                signs.addAll(
                        signsDto.getSigns()
                                .stream()
                                .map(signDto -> Mapper.mapSignDtoToSign(signDto, flow))
                                .toList()
                );
                List<SemaphoreDto> semaphoresDto = remoteRepository.getTrafficLights(flowDto.getRoadId(), flowDto.getFlowId());
                trafficLights.addAll(
                                semaphoresDto.stream()
                                .map(semaphoreDto-> Mapper.mapSemaphoreDtoToSemaphore(semaphoreDto, flow))
                                .toList()
                );
            }
            this.path = new Path(pathSegments);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getPath() {
        return path;
    }

    public List<Flow> getFlows() {
        return List.copyOf(flows);
    }

    public List<RoadElement> getRoadElements() {
        List<RoadElement> elements = new ArrayList<>();
        elements.addAll(trafficLights);
        elements.addAll(signs);
        return elements;
    }

    public List<TrafficLight> getTrafficLights() {
        return List.copyOf(trafficLights);
    }
}
