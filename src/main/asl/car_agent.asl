/* -------------------------------------------------
   Initial beliefs and rules
---------------------------------------------------*/
stop.
red.
yellow.
green.

driver.
autonomous.
mode(driver).


car_on.
car_off.

car_state(car_off).

break_speed(5).
drag_speed(1).
reaction_time(1).

near(X1, Y1, X2, Y2, DN) :-
    distance(X1, Y1, X2, Y2, D) & D < DN.

distance_to_stop(CS, DECEL, SD) :-
    reaction_time(RT)
    & SD = (CS * CS) / (2 * DECEL) + (CS * RT).

stopping_distance(CS, SD) :- break_speed(BS) & distance_to_stop(CS, BS, SD).
dragging_distance(CS, SD) :- drag_speed(DS) & distance_to_stop(CS, DS, SD).

stopping(X1, Y1, X2, Y2, CS) :- distance(X1, Y1, X2, Y2, D)
                                & stopping_distance(CS, SD)
                                & D < SD + 10.

dragging(X1, Y1, X2, Y2, CS) :- distance(X1, Y1, X2, Y2, D)
                                 & dragging_distance(CS, SD)
                                 & D < SD + 10.

distance(X1, Y1, X2, Y2, D) :-
    utils.sqrt(D, (X1 - X2)*(X1 - X2) + (Y1 - Y2)*(Y1 - Y2)).

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
arrest_car_event(E, SX, SY) :- element(stop, SX, SY) & E = stop.
arrest_car_event(E, SX, SY) :- element(traffic_light(red), SX, SY) & E = traffic_light(red).
arrest_car_event(E, SX, SY) :- element(traffic_light(yellow), SX, SY) & E = traffic_light(yellow).

/* -------------------------------------------------
   Events
---------------------------------------------------*/
+position(X, Y) <-
    .print("Current position: (", X, ", ", Y, ")").

+restart <-
    .print("Restarting the car agent");
    !reachSpeedLimit.

+autonomous_mode <-
    .print("Switching to autonomous driving mode");
    -mode(driver);
    +mode(autonomous);
    !reachSpeedLimit.

+driver_mode <-
    .print("Switching to driver mode (manual control)");
    -mode(autonomous);
    +mode(driver).

+car_started <-
    .print("Car engine started");
    -car_state(car_off);
    +car_state(car_on);
    !move_car.


+car_stopped <-
    .print("Car engine stopped");
    -car_state(car_on);
    +car_state(car_off).


+car(D, S) <-
    .print("Detected car ahead ", D, "moving at speed ", S).

/* When the light turns green (environment percept updated) */
+element(traffic_light(green), SX, SY) : near(X, Y, SX, SY, 10) <-
    .print("Traffic light at (", SX, ", ", SY, ") turned green, resuming driving");
    !reachSpeedLimit.

/* -------------------------------------------------
   Initial goals
---------------------------------------------------*/

/* -------------------------------------------------
   Plans
---------------------------------------------------*/
+!move_car : destination(SX, SY)
             & position(X, Y)
             & distance(X, Y, SX, SY, D)
             & D < 10 <-
    .print("Car arrived").

+!move_car : car_state(car_on) <-
    .print("Moving car to new position");
    .wait(1000);
    move;
    !move_car.

+!move_car : car_state(car_off) <-
    .print("Car is off, cannot move").

/* 0. Disable autonomous driving */
+!reachSpeedLimit : mode(driver) <-
    .print("Autonomous driving is disabled").

+!reachSpeedLimit : car_state(car_off) <-
    -mode(autonomous);
    +mode(driver);
    .print("Car is off, cannot reach speed limit").

/* 1. Handle stop signs and traffic lights */
/*
+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & currentSpeed(CS)
                    & position(X, Y)
                    & distance(X, Y, SX, SY, D)
                     <-
    .print("Detected ", E, " element at (", SX, ", ", SY, ")");
    .print("Speed is ", CS, ", need to approach and stop if needed");
    .print("Position is (", X, ", ", Y, ")");
    .print("Distance to ", E, " element is ", D, "m");
    .wait(1000);
    !reachSpeedLimit.
*/

+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & currentSpeed(CS)
                    & position(X, Y)
                    & stopping(X, Y, SX, SY, CS)
                    & CS > 0 <-
    .print("Approaching ", E, " element at (", SX, ", ", SY, "), slowing down with brakes");
    brake;
    .wait(1000);
    !reachSpeedLimit.


+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & currentSpeed(CS)
                    & position(X, Y)
                    & dragging(X, Y, SX, SY, CS)
                    & CS > 15 <-
    .print("Approaching ", E, " element at (", SX, ", ", SY, "), slowing down");
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.


+!reachSpeedLimit : arrest_car_event(E, SX, SY)
                    & position(X, Y)
                    & near(X, Y, SX, SY, 10)
                    & currentSpeed(0) <-
    .print("Stopped at ", E, " sign (", SX, ", ", SY, "), waiting before resuming");
    .wait(2000);
    passedStop(SX, SY);
    -E;
    .print("STOP complete, wait to resume driving").

/* 2. Maintain safe distance from other cars */

/* When the car ahead is too close — brake (dynamic distance) */
+!reachSpeedLimit : car(D, OS)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & CS > OS
                    & dynamic_safe_distance(CS, OS, DSD)
                    & D < DSD <-
    .print("Too close to car ahead at (", X, ", ", Y, "), brake. Dynamic safe distance = ", DSD, "m, actual = ", D, "m");
    brake;
    .wait(1000);
    !reachSpeedLimit.

/* When the car ahead is at normal safe distance — slow down */
+!reachSpeedLimit : car(D, S)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & dynamic_safe_distance(CS, S, DSD)
                    & normal_safe_distance(S, NSD)
                    & D >= DSD
                    & D <= NSD <-
    .print("Maintaining safe distance (", D, "m) behind car at (", X, ", ", Y, ") — NSD = ", NSD);
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.

/* When the car ahead is far but not to far keep speed*/
+!reachSpeedLimit : car(D, S)
                    & position(CX, CY)
                    & currentSpeed(CS)
                    & dynamic_safe_distance(CS, S, DSD)
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
