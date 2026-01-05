package repository;

import model.CarUpdate;
import model.OtherCar;
import model.math.Point;
import model.path.Flow;
import model.path.PathSegment;
import model.roadElement.Sign;
import model.roadElement.SpeedLimitSign;
import model.roadElement.TrafficLight;
import repository.dto.car.CarUpdateMessage;
import repository.dto.car.OtherCarMessage;
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

    public static OtherCar mapOtherCarDtoToOtherCar(
            OtherCarMessage otherCarMessage
    ) {
        return new OtherCar(
                otherCarMessage.getSpeed(),
                otherCarMessage.getDistance()
        );
    }

    public static CarUpdateMessage mapCarUpdateToCarUpdateMessage(
            CarUpdate update
    ) {
        return new CarUpdateMessage(
                update.getCar().getPlate(),
                update.getCar().getSpeed(),
                0,
                "RUNNING",
                update.getCar().getPosition().getX(),
                update.getCar().getPosition().getY(),
                update.getIndexFlow(),
                update.getDistance().getX(),
                update.getDistance().getY()
        );
    }


}
