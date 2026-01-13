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
drag_speed(0.5).
reaction_time(1).
car_distance(70).

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
    utils.sqrt(D, (X1 - X2) * (X1 - X2) + (Y1 - Y2)*(Y1 - Y2)).

/* Safe distance rules */

/* Normal safe distance: proportional to front car speed */
normal_safe_distance(S, SD) :- car_distance(D) & SD = S / 2 + 10 + D.

/* Dynamic safe distance: based on relative speed (if faster than front car) */
dynamic_safe_distance(CS, OS, SD) :-
    car_distance(D) &
    Diff = CS - OS &
    Diff > 0 &
    SD = D + 10 + Diff * 2.

dynamic_safe_distance(CS, OS, SD) :-
    Diff = CS - OS &
    Diff <= 0 &
    normal_safe_distance(S, SD).

/* Define which elements require the car to stop or slow down */
arrest_car_event(E, SX, SY) :- element(stop, SX, SY) & E = stop.
arrest_car_event(E, SX, SY) :- element(traffic_light(red), SX, SY) & E = traffic_light(red).
arrest_car_event(E, SX, SY) :- element(traffic_light(yellow), SX, SY) & E = traffic_light(yellow).

/* -------------------------------------------------
   Hazard levels
---------------------------------------------------*/

hazard(Level, E) :-
    arrest_car_event(E, SX, SY)
    & currentSpeed(CS)
    & position(X, Y)
    & stopping(X, Y, SX, SY, CS)
    & CS > 0
    & Level = 2.

hazard(Level, E) :-
    car(D, OS)
    & position(CX, CY)
    & currentSpeed(CS)
    & CS > OS
    & dynamic_safe_distance(CS, OS, DSD)
    & D < DSD
    & Level = 2
    & E = car(D, OS).

hazard(Level, E) :-
    arrest_car_event(E, SX, SY)
    & currentSpeed(CS)
    & position(X, Y)
    & dragging(X, Y, SX, SY, CS)
    & CS > 15
    & Level = 1.

hazard(Level, E) :-
    car(D, S)
    & position(CX, CY)
    & currentSpeed(CS)
    & dynamic_safe_distance(CS, S, DSD)
    & normal_safe_distance(S, NSD)
    & D >= DSD
    & D <= NSD
    & Level = 1
    & E = car(D, S).

hazard(Level, E) :-
    arrest_car_event(E, SX, SY)
    & position(X, Y)
    & near(X, Y, SX, SY, 10)
    & currentSpeed(0)
    & Level = 0.

hazard(Level, E) :-
    car(CD, S)
    & car_distance(D)
    & currentSpeed(0)
    & CD < D
    & Level = 0
    & E = car(CD, S).

/* Determine hazard level based on current speed and speed limit */

hazard(Level, E) :-
    currentSpeed(CS)
    & speedLimit(SL)
    & CS > SL + 20
    & Level = 2
    & E = speed_limit_violation.

hazard(Level, E) :-
    currentSpeed(CS)
    & speedLimit(SL)
    & CS > SL
    & CS <= SL + 20
    & Level = 1
    & E = speed_limit_violation.

hazard(Level, E) :-
    currentSpeed(CS)
    & speedLimit(SL)
    & CS <= SL
    & CS >= SL - 5
    & Level = 0
    & E = no_hazard.

hazard(Level, E) :-
    currentSpeed(CS)
    & speedLimit(SL)
    & CS < SL
    & Level = -1
    & E = less_hazard.

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
    .print("Detected car ahead ", D, " moving at speed ", S).

/* When the light turns green (environment percept updated) */
+element(traffic_light(green), SX, SY) :  position(X, Y)
        & near(X, Y, SX, SY, 10) <-
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

/* 1. React speed limit to avoid hazards based on their level */

+!reachSpeedLimit : hazard(2, E) <-
    .print("Moderate hazard detected, braking");
    .print("Hazard event: ", E);
    brake;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : hazard(1, E) <-
    .print("Minor hazard detected, slowing down slightly");
    .print("Hazard event: ", E);
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : hazard(0, E) <-
    .print("No hazards detected, keeping speed");
    .print("Hazard event: ", E);
    keep_speed;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit : hazard(-1, E) <-
    .print("No hazards detected, proceeding to reach speed limit");
    .print("Hazard event: ", E);
    accelerate;
    .wait(1000);
    !reachSpeedLimit.

+!reachSpeedLimit <-
    .print("No plans applicable, slowing down to be safe and avoid potential problems");
    do_nothing;
    .wait(1000);
    !reachSpeedLimit.
