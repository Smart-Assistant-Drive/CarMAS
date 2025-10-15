
/* Initial beliefs and rules */
near(X1, Y1, X2, Y2) :- DX = abs(X1 - X2) & DY = abs(Y1 - Y2) & DX < 5 & DY < 5.

/* Events */
+position(X, Y) <-
    .print("Current position: (", X, ", ", Y, ")").

+restart <-
    .print("Restarting the car agent");
    !reachSpeedLimit.

/* Initial goals */
!move_car.
!reachSpeedLimit.

/* Plans */

+!move_car <-
    .print("Moving car to new position");
    .wait(1000);
    move;
    !move_car.

+!reachSpeedLimit : sign(STOP, SX, SY) & currentSpeed(CS) & position(X, Y) & near(X, Y, SX, SY) & CS > 0 <-
    .print("Approaching STOP sign at (", SX, ", ", SY, "), slowing down");
    brake;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : sign(STOP, SX, SY) & position(X, Y) & near(X, Y, SX, SY) & currentSpeed(0) <-
    .print("Stopped at STOP sign (", SX, ", ", SY, "), waiting before resuming");
    .wait(2000);
    passedStop(SX, SY);
    -sign(STOP, SX, SY);
    .print("STOP complete, wait to resume driving").

+!reachSpeedLimit : currentSpeed(CS) & speedLimit(SL) & CS < SL <-
    .print("Current speed: ", CS, ", below speed limit: ", SL);
    .print("Speeding up to reach speed limit");
    accelerate;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS) & speedLimit(SL) & CS = SL <-
    .print("Current speed: ", CS, ", at speed limit: ", SL);
    .print("Maintaining current speed");
    keep_speed;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS) & speedLimit(SL) & CS > SL + 20 <-
    .print("Current speed: ", CS, ", above speed limit: ", SL);
    .print("Slowing down to reach speed limit");
    brake;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS) & speedLimit(SL) & CS > SL & CS <= SL + 20 <-
    .print("Current speed: ", CS, ", slightly above speed limit: ", SL);
    .print("Gently slowing down to reach speed limit");
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.

