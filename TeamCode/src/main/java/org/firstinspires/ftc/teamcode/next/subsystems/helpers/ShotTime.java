package org.firstinspires.ftc.teamcode.next.subsystems.helpers;

public class ShotTime {
    ShotTime() {

    }
    /**
     * @param distance
     * @return Time to shoot based on distance
     */
    public static double get(double distance) {
        double b = 0.216777;
        double m = 0.00559718;
        return m *distance+ b;
    }

}