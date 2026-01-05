package repository;

import model.math.Point;
import model.path.Flow;
import model.path.PathSegment;
import model.roadElement.Sign;
import model.roadElement.SpeedLimitSign;
import model.roadElement.TrafficLight;
import repository.dto.path.PathSegmentDto;
import repository.dto.semaphore.SemaphoreDto;
import repository.dto.sign.SignResponseDto;
import repository.dto.util.CoordinateDto;

import static model.roadElement.Sign.SignType.*;

public class Mapper {

    public static Point mapCoordinateDtoToPoint(
            CoordinateDto coordinateDto
    ) {
        return new Point(
                coordinateDto.getX(),
                coordinateDto.getY()
        );
    }

    public static PathSegment mapPathSegmentDtoToPathSegment(
            PathSegmentDto pathSegmentDto, Flow flow
    ) {
        return new PathSegment(
                flow,
                mapCoordinateDtoToPoint(pathSegmentDto.getFrom()),
                mapCoordinateDtoToPoint(pathSegmentDto.getTo())
        );
    }

    public static Sign mapSignDtoToSign(
            SignResponseDto signDto,
            Flow flow
    ) {
        Point position = new Point(signDto.getLatitude(), signDto.getLongitude());
        switch (signDto.getType()) {
            case "MaximumSpeedLimitSign" -> {
                return new SpeedLimitSign(
                        position,
                        flow,
                        signDto.getSpeedLimit()
                );
            }
            case "stop" -> {
                return new Sign(
                        position,
                        flow,
                        STOP
                );
            }
            case "yield" -> {
                return new Sign(
                        position,
                        flow,
                        YIELD
                );
            }
            default -> throw new IllegalArgumentException("Unknown sign type: " + signDto.getType());
        }
    }

    public static TrafficLight mapSemaphoreDtoToSemaphore(
            SemaphoreDto semaphoreDto,
            Flow flow
    ) {
        return new TrafficLight(
                mapCoordinateDtoToPoint(semaphoreDto.getPositionDto()),
                flow
        );
    }


}
