package model;

import jason.asSyntax.Literal;
import model.math.Point;
import model.roadElement.Sign;
import model.roadElement.TrafficLight;

public class LiteralConverter {
    
    public static String POSITION_BASE = "position(X, Y)";
    public static String CURRENT_SPEED_BASE = "currentSpeed(CS)";
    public static String SPEED_LIMIT_BASE = "speedLimit(LIMIT)";
    public static String SIGN_TYPE_BASE = "element(TYPE, X, Y)";
    public static String TRAFFIC_LIGHT_BASE = "element(traffic_light(STATE), X, Y)";

    public static Literal positionToLiteral(Point p) {
        return Literal.parseLiteral(POSITION_BASE.replace("X", String.valueOf(p.getX()))
                .replace("Y", String.valueOf(p.getY())));
    }

    public static Literal currentSpeedToLiteral(double speed) {
        return Literal.parseLiteral(CURRENT_SPEED_BASE.replace("CS", String.valueOf(speed)));
    }

    public static Literal speedLimitToLiteral(int speedLimit) {
        return Literal.parseLiteral(SPEED_LIMIT_BASE.replace("LIMIT", String.valueOf(speedLimit)));
    }

    public static Literal signTypeToLiteral(Sign.SignType signType) {
        return Literal.parseLiteral(SIGN_TYPE_BASE.replace("TYPE", signType.toString().toLowerCase()));
    }

    public static Literal signToLiteral(Sign s) {
        return Literal.parseLiteral(SIGN_TYPE_BASE.replace("TYPE", s.getType().toString().toLowerCase())
                .replace("X", String.valueOf(s.getPosition().getX()))
                .replace("Y", String.valueOf(s.getPosition().getY())));
    }

    public static Literal trafficLightToLiteral(TrafficLight tl) {
        return Literal.parseLiteral(TRAFFIC_LIGHT_BASE.replace("STATE", tl.getState().toString().toLowerCase())
                .replace("X", String.valueOf(tl.getPosition().getX()))
                .replace("Y", String.valueOf(tl.getPosition().getY())));
    }
}
