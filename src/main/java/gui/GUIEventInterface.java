package gui;

public interface GUIEventInterface {

    void onInsertLicense(String licenseNumber);

    void onStartCar();

    void onStopCar();

    void onSpeedChange(double newSpeed);

    void onToggleAutonomousModeChange(boolean isAutonomous);
}
