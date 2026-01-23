package org.firstinspires.ftc.teamcode.wayfinder;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.RADIANS;
import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.MM;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

/*
 * This class takes an current position, and a target position, and calculates the motor power
 * output required by each motor to drive to that point. It uses three PID loops to find the power
 * required to move in x, y, and yaw (heading) respectively.
 */

public class Wayfinder {

    private enum Direction {
        x,
        y,
        h
    }

    private enum InBounds {
        NOT_IN_BOUNDS,
        IN_X_Y,
        IN_HEADING,
        IN_BOUNDS
    }

    private static double xyTolerance = 12; // How close to the target do we need to be in mm
    private static double yawTolerance = 0.0349066; // Heading tolerance in radians, 2°.

    private static double pGain = 0.008; // For the X and Y positions
    private static double dGain = 0.00001;
    private static double accel = 10.0;

    private static double yawPGain = 5.0; // For the Yaw
    private static double yawDGain = 0.0;
    private static double yawAccel = 20.0;

    private double frontLeftMotorOutput  = 0;
    private double frontRightMotorOutput = 0;
    private double backLeftMotorOutput   = 0;
    private double backRightMotorOutput  = 0;

    private final ElapsedTime holdTimer = new ElapsedTime();
    private final ElapsedTime PIDTimer = new ElapsedTime();

    //private LinearOpMode myOpMode; //todo: consider if this is required

    private final PIDLoop xPID = new PIDLoop();
    private final PIDLoop yPID = new PIDLoop();
    private final PIDLoop hPID = new PIDLoop();

    private GoBildaPinpointDriver pinpointDriver;

//    public Wayfinder(LinearOpMode opmode){
//        myOpMode = opmode;
//    }

    public Wayfinder(
            GoBildaPinpointDriver pinpointDriver
    ){
        this.pinpointDriver = pinpointDriver;
    }

    /**
     * Resets the IMU and sets up anything else that needs to be reset for later
     */
    public void init() {
        pinpointDriver.resetPosAndIMU();
    }

    /**
     * Sets the PID coefficients for the X/Y axis. In most implementations, changing this is not
     * necessary.
     * @param p the P gain.
     * @param d the D gain.
     * @param acceleration the maximum change in motor power allowed per second. A value of 10 allows
     *                     the motor power to slew from 0 to 1 in 0.1s.
     * @param unit the unit to use for the tolerance.
     * @param tolerance the maximum deviation from the target position allowed.
     */
    public void setXYCoefficients(double p, double d, double acceleration, DistanceUnit unit, double tolerance){
        pGain = p;
        dGain = d;
        accel = acceleration;
        xyTolerance = unit.toMm(tolerance);
    }

    /**
     * Sets the PID coefficients for the yaw (heading) axis. In most implementations, changing this is not
     * necessary.
     * @param p the P gain.
     * @param d the D gain.
     * @param acceleration the maximum change in motor power allowed per second. A value of 10 allows
     *                     the motor power to slew from 0 to 1 in 0.1s.
     * @param unit the unit to use for the tolerance.
     * @param tolerance the maximum deviation from the target position allowed.
     */
    public void setYawCoefficients(double p, double d, double acceleration, AngleUnit unit, double tolerance){
        yawPGain = p;
        yawDGain = d;
        yawAccel = acceleration;
        yawTolerance = unit.toRadians(tolerance);
    }

    /**
     * @return the output power to apply to the front left drive motor.
     */
    public double getFrontLeftMotorOutput(){
        return frontLeftMotorOutput;
    }

    /**
     * @return the output power to apply to the front right drive motor.
     */
    public double getFrontRightMotorOutput(){
        return frontRightMotorOutput;
    }

    /**
     * @return the output power to apply to the back left drive motor.
     */
    public double getBackLeftMotorOutput(){
        return backLeftMotorOutput;
    }

    /**
     * @return the output power to apply to the back right drive motor.
     */
    public double getBackRightMotorOutput(){
        return backRightMotorOutput;
    }

    /**
     * Whether or not we are at our target position
     */
    boolean atTarget = false;

    /**
     * The core function of this library, run this with your current and target position to ask the
     * library to calculate the required power from each drive motor.
     * @param targetPosition A {@link Pose2D} containing the target robot position.
     * @param maxPower A {@link Double} from 0-1, the maximum motor power to be applied during this movement.
     * @param holdTime A {@link Double} that is the number of seconds to hold at the target before returning true.
     * @return true if target position has been met for holdTime number of seconds.
     */
    public boolean driveTo(Pose2D targetPosition, double maxPower, double holdTime) {
        Pose2D currentPosition = pinpointDriver.getPosition();
        /*
         * Calculate the global (field-centric) outputs for each axis.
         */
        double xPWR = calculatePID(currentPosition, targetPosition, Direction.x);
        double yPWR = calculatePID(currentPosition, targetPosition, Direction.y);
        double hOutput = calculatePID(currentPosition, targetPosition, Direction.h);

        /*
         * Calculate the sine and cosine of our heading to transform our global power outputs
         * from the PID loops to robot-centric power.
         */
        double heading = currentPosition.getHeading(RADIANS);
        double cosine = Math.cos(heading);
        double sine = Math.sin(heading);

        double xOutput = (xPWR * cosine) + (yPWR * sine);
        double yOutput = (xPWR * -sine) + (yPWR * cosine);

        /*
         * Send our robot centric power (constrained to the maximum power allowed) to mix the three
         * inputs into four motor power outputs for a mecanum drive.
         */
        calculateMecanumOutput(
                Math.min(maxPower, Math.max(-maxPower, xOutput)),
                Math.min(maxPower, Math.max(-maxPower, yOutput)),
                Math.min(maxPower, Math.max(-maxPower, hOutput))
        );

        /*
         * Check and see if we are within a tolerance of the target position in every axis.
         */
        if(inBounds(currentPosition, targetPosition) == InBounds.IN_BOUNDS){
            atTarget = true;
        } else {
            holdTimer.reset();
            atTarget = false;
        }

        return atTarget && holdTimer.time() > holdTime;
    }

    /**
     * The core function of this library, run this with your current and target position to ask the
     * library to calculate the required power from each drive motor.
     * @param targetPosition A {@link Pose2D} containing the target robot position.
     * @param maxPower A {@link Double} from 0-1, the maximum motor power to be applied during this movement.
     * @return true if target position has been met.
     */
    public boolean driveTo(Pose2D targetPosition, double maxPower) {
        return driveTo(targetPosition, maxPower, 0.0);
    }


    /**
     * The core function of this library, run this with your current and target position to ask the
     * library to calculate the required power from each drive motor.
     * @param targetPosition A {@link Pose2D} containing the target robot position.
     * @return true if target position has been met
     */
    public boolean driveTo(Pose2D targetPosition) {
        return driveTo(targetPosition, 1.0, 0.0);
    }

    /**
     * This is a standard turn to function
     * @param targetHeading A {@link Double} containing the target heading in radians.
     * @param maxPower A {@link Double} from 0-1, the maximum motor power to be applied during this movement.
     * @param holdTime A {@link Double} that is the number of seconds to hold at the target before returning true.
     * @return A {@link Boolean} that's true if target position has been met for holdTime number of seconds.
     */
    public boolean turnTo(double targetHeading, double maxPower, double holdTime) {
        Pose2D currentPosition = pinpointDriver.getPosition();

        return driveTo(new Pose2D(MM, currentPosition.getX(MM), currentPosition.getY(MM), RADIANS, targetHeading), maxPower, holdTime);
    }

    /**
     * This is a standard turn to function
     * @param targetHeading A {@link Double} containing the target heading in radians.
     * @param maxPower A {@link Double} from 0-1, the maximum motor power to be applied during this movement.
     * @return A {@link Boolean} that's true if target position has been met
     */
    public boolean turnTo(double targetHeading, double maxPower) {
        return turnTo(targetHeading, maxPower, 0.0);
    }

    /**
     * This is a standard turn to function
     * @param targetHeading A {@link Double} containing the target heading in radians.
     * @return A {@link Boolean} that's true if target position has been met
     */
    public boolean turnTo(double targetHeading) {
        return turnTo(targetHeading, 1.0, 0.0);
    }

    /**
     * This is a standard mecanum mix function.
     * @param forward 0-1, requested forward drive power.
     * @param strafe 0-1, requested strafe drive power.
     * @param yaw 0-1, requested yaw (heading) drive power.
     */
    private void calculateMecanumOutput(double forward, double strafe, double yaw) {
        double leftFront = forward - strafe - yaw;
        double rightFront = forward + strafe + yaw;
        double leftBack = forward + strafe - yaw;
        double rightBack = forward - strafe + yaw;

        double max = Math.max(Math.abs(leftFront), Math.abs(rightFront));
        max = Math.max(max, Math.abs(leftBack));
        max = Math.max(max, Math.abs(rightBack));

        if (max > 1.0) {
            leftFront /= max;
            rightFront /= max;
            leftBack /= max;
            rightBack /= max;
        }

        frontLeftMotorOutput  = leftFront;
        frontRightMotorOutput = rightFront;
        backLeftMotorOutput   = leftBack;
        backRightMotorOutput  = rightBack;
    }

    /**
     * Calculate the PID value for a particular axis based on two Pose2D.
     * @param currentPosition A Pose2D Containing the robot's estimated position
     * @param targetPosition A Pose2D Containing the robot's target position.
     * @param direction X, Y, or H. The direction to calculate.
     * @return 0-1, the power to apply towards moving the robot in the specified axis.
     */
    private double calculatePID(Pose2D currentPosition, Pose2D targetPosition, Direction direction){
        double output = 0;
        switch (direction){
            case x:
                double xError = targetPosition.getX(MM) - currentPosition.getX(MM);
                output = xPID.calculateAxisPID(xError, pGain, dGain, accel,PIDTimer.seconds());
                break;
            case y:
                double yError = targetPosition.getY(MM) - currentPosition.getY(MM);
                output = yPID.calculateAxisPID(yError, pGain, dGain, accel, PIDTimer.seconds());
                break;
            case h:
                double hError = targetPosition.getHeading(RADIANS) - currentPosition.getHeading(RADIANS);
                output = hPID.calculateAxisPID(hError, yawPGain, yawDGain, yawAccel, PIDTimer.seconds());
                break;
        }
        return output;
    }

    /**
     * Check and see if we are within a tolerance of the target position.
     * @param currPose A Pose2D containing the robot's current position.
     * @param trgtPose A Pose2D containing the robot's target position.
     * @return IN_BOUNDS if we are within tolerance on every axis. IN_X_Y if heading is still out
     * of range, IN_HEADING if X and Y are not both in range. NOT_IN_BOUNDS if all are out of tolerance.
     */
    private InBounds inBounds (Pose2D currPose, Pose2D trgtPose){
        boolean xInBounds = currPose.getX(MM) > (trgtPose.getX(MM) - xyTolerance) && currPose.getX(MM) < (trgtPose.getX(MM) + xyTolerance);
        boolean yInBounds = currPose.getY(MM) > (trgtPose.getY(MM) - xyTolerance) && currPose.getY(MM) < (trgtPose.getY(MM) + xyTolerance);
        boolean hInBounds = currPose.getHeading(RADIANS) > (trgtPose.getHeading(RADIANS) - yawTolerance) &&
                currPose.getHeading(RADIANS) < (trgtPose.getHeading(RADIANS) + yawTolerance);

        if (xInBounds && yInBounds && hInBounds){
            return InBounds.IN_BOUNDS;
        } else if (xInBounds && yInBounds){
            return InBounds.IN_X_Y;
        } else if (hInBounds){
            return InBounds.IN_HEADING;
        } else
            return InBounds.NOT_IN_BOUNDS;
    }

}