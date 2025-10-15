/* -------------------------------------------------
   Initial beliefs and rules
---------------------------------------------------*/
stop.
red.
yellow.
green.

near(X1, Y1, X2, Y2) :- DX = abs(X1 - X2)
                        & DY = abs(Y1 - Y2)
                        & DX < 5
                        & DY < 5.

distance(X1, Y1, X2, Y2, D) :- D = sqrt((X1 - X2)*(X1 - X2) + (Y1 - Y2)*(Y1 - Y2)).

/* Safe distance rules */

/* Normal safe distance: proportional to front car speed */
normal_safe_distance(S, SD) :- SD = S / 2 + 10.

/* Dynamic safe distance: based on relative speed (if faster than front car) */
dynamic_safe_distance(CS, OS, SD) :-
    Diff = CS - OS &
    Diff > 0 &
    SD = 10 + Diff * 2.

dynamic_safe_distance(CS, OS, SD) :-
    Diff = CS - OS &
    Diff <= 0 &
    normal_safe_distance(S, SD).

/* Define which elements require the car to stop or slow down */
arrest_car_event(E, SX, SY) :- E = element(stop, SX, SY).
arrest_car_event(E, SX, SY) :- E = element(traffic_light(red), SX, SY).
arrest_car_event(E, SX, SY) :- E = element(traffic_light(yellow), SX, SY).

/* -------------------------------------------------
   Events
---------------------------------------------------*/
+position(X, Y) <-
    .print("Current position: (", X, ", ", Y, ")").

+restart <-
    .print("Restarting the car agent");
    !reachSpeedLimit.

+car(X, Y, S) <-
    .print("Detected car ahead at (", X, ", ", Y, ") moving at speed ", S).

/* When the light turns green (environment percept updated) */
+element(traffic_light(green), SX, SY) : position(X, Y) & near(X, Y, SX, SY) <-
    .print("Traffic light at (", SX, ", ", SY, ") turned green, resuming driving");
    !reachSpeedLimit.

/* -------------------------------------------------
   Initial goals
---------------------------------------------------*/

!move_car.
!reachSpeedLimit.

/* -------------------------------------------------
   Plans
---------------------------------------------------*/

+!move_car <-
    .print("Moving car to new position");
    .wait(1000);
    move;
    !move_car.

/* 1. Handle stop signs and traffic lights */

+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & currentSpeed(CS)
                    & position(X, Y)
                    & near(X, Y, SX, SY)
                    & CS > 0 <-
    .print("Approaching ", E, " element at (", SX, ", ", SY, "), slowing down");
    brake;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & position(X, Y)
                    & near(X, Y, SX, SY)
                    & currentSpeed(0) <-
    .print("Stopped at ", E, " sign (", SX, ", ", SY, "), waiting before resuming");
    .wait(2000);
    passedStop(SX, SY);
    -E;
    .print("STOP complete, wait to resume driving").

/* 2. Maintain safe distance from other cars */

/* When the car ahead is too close — brake (dynamic distance) */
+!reachSpeedLimit : car(X, Y, OS)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & CS > OS
                    & dynamic_safe_distance(CS, OS, DSD)
                    & distance(CX, CY, X, Y, D)
                    & D < DSD <-
    .print("Too close to car ahead at (", X, ", ", Y, "), brake. Dynamic safe distance = ", DSD, "m, actual = ", D, "m");
    brake;
    .wait(1000);
    !reachSpeedLimit.

/* When the car ahead is at normal safe distance — slow down */
+!reachSpeedLimit : car(X, Y, S)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & dynamic_safe_distance(CS, S, DSD)
                    & normal_safe_distance(S, NSD)
                    & distance(CX, CY, X, Y, D)
                    & D >= DSD
                    & D <= NSD <-
    .print("Maintaining safe distance (", D, "m) behind car at (", X, ", ", Y, ") — NSD = ", NSD);
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.

/* When the car ahead is far but not to far keep speed*/
+!reachSpeedLimit : car(X, Y, S)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & dynamic_safe_distance(CS, S, DSD)
                    & distance(CX, CY, X, Y, D)
                    & D > DSD
                    & D <= NSD + 20 <-
    .print("Car ahead at (", X, ", ", Y, ") is far but not too far (", D, "m) — NSD = ", NSD);
    keep_speed;
    .wait(1000);
    !reachSpeedLimit.

/* 3. Adjust speed to reach speed limit */

+!reachSpeedLimit : currentSpeed(CS)
                    & speedLimit(SL)
                    & CS < SL <-
    .print("Current speed: ", CS, ", below speed limit: ", SL);
    .print("Speeding up to reach speed limit");
    accelerate;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS)
                    & speedLimit(SL)
                    & CS = SL <-
    .print("Current speed: ", CS, ", at speed limit: ", SL);
    .print("Maintaining current speed");
    keep_speed;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS)
                    & speedLimit(SL)
                    & CS > SL + 20 <-
    .print("Current speed: ", CS, ", above speed limit: ", SL);
    .print("Slowing down to reach speed limit");
    brake;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : currentSpeed(CS)
                    & speedLimit(SL)
                    & CS > SL
                    & CS <= SL + 20 <-
    .print("Current speed: ", CS, ", slightly above speed limit: ", SL);
    .print("Gently slowing down to reach speed limit");
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.

