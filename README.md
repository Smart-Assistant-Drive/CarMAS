# CarMAS

------

Car mas is an agent that controls a car on the road.

It uses BDI architecture to control the car.

## Features

- BDI architecture
- Car control
- Speed control
- Check for traffic lights or stop signs
- Avoid collisions with other cars
- change roads following a given path
- respect speed limits

## Requirements
- Java 11 or higher
- Gradle
- The SAD System up and running
- The scene of the environment where the car will be running set up in the SAD System

## Run
To run the CarMAS agent, you need to have Java installed on your machine. You can then compile and run the agent using the following commands:

```bash
./gradlew runcarMAS
```