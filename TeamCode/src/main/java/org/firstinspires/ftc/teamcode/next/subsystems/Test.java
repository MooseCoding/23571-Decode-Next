package org.firstinspires.ftc.teamcode.next.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.FeedbackCRServoEx;

public class Test implements Subsystem {
    public static double previousAngle = 0.0;
    public static double totalAngle = 0.0; // A summation of our angles
    public static double deltaHeading = 0.0; // Change in heading
    public static double gearRatio = 83/27; // Your gear ratio

    private FeedbackCRServoEx servo = new FeedbackCRServoEx(
        0.1,
            () -> ActiveOpMode.hardwareMap().analogInput.get("encoderServo"),
            () -> ActiveOpMode.hardwareMap().crservo.get("turret_servo")
    );

    public static PIDCoefficients pidCoefficients = new PIDCoefficients(0.0,0.0,0.0);

    private ControlSystem turretControl = ControlSystem.builder()
            .posPid(pidCoefficients)
            .build();

    public double updateTurretAngle() {
        double currentAngle = servo.getCurrentPosition();
        deltaHeading = currentAngle - previousAngle;

        if (deltaHeading > Math.PI) deltaHeading -= 2 * Math.PI;
        else if (deltaHeading < -Math.PI) deltaHeading += 2 * Math.PI;

        totalAngle += deltaHeading;
        previousAngle = currentAngle;

        double turretAngle = (totalAngle/gearRatio) % (2*Math.PI);
        if (turretAngle < 0) turretAngle+=2*Math.PI;
        return turretAngle;
    }
}
