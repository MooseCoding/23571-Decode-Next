package org.firstinspires.ftc.teamcode.wayfinder;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.wayfinder.Wayfinder;

import java.util.Locale;

/*
 * This example auto shows how to use the "Wayfinder" point to point class to drive your robot
 * between different target positions.
 */

@Autonomous(name="Odometry Navigation Example", group="Pinpoint")
//@Disabled

public class ConceptWayfinder extends LinearOpMode {

    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    GoBildaPinpointDriver pinpoint; // Declare OpMode member for the Odometry Computer

    Wayfinder wayfinder; //OpMode member for the point-to-point navigation class

    /*
     * Here we create a state machine which will capture the different parts of our auto.
     * Each state captures a distinct step in the autonomous period, in this case we have one for
     * driving to each of our 5 target positions.
     */
    enum StateMachine {
        WAITING_FOR_START,
        AT_TARGET,
        DRIVE_TO_TARGET_0,
        DRIVE_TO_TARGET_1,
        DRIVE_TO_TARGET_2,
    }
    StateMachine stateMachine = StateMachine.WAITING_FOR_START;

    /*
     * Create a series of Pose2D targets, each of these will be a step in our autonomous.
     */
    static final Pose2D TARGET_0 = new Pose2D(DistanceUnit.MM,800,0,AngleUnit.DEGREES,0);
    static final Pose2D TARGET_1 = new Pose2D(DistanceUnit.MM, 1600, 0, AngleUnit.DEGREES, -90);
    static final Pose2D TARGET_2 = new Pose2D(DistanceUnit.MM,800,600, AngleUnit.DEGREES,-90);

    @Override
    public void runOpMode() {

        initializeMotors(); // Init and configure our motors.

        initializePinpoint(); // Init and configure out Pinpoint.

        wayfinder = new Wayfinder(pinpoint);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", pinpoint.getXOffset(DistanceUnit.MM));
        telemetry.addData("Y offset", pinpoint.getYOffset(DistanceUnit.MM));
        telemetry.addData("Device Version Number:", pinpoint.getDeviceVersion());
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        resetRuntime();

        while (opModeIsActive()) {
            pinpoint.update(); // Get new data from our Pinpoint for us to act on.

            switch (stateMachine){
                case WAITING_FOR_START:
                    //the first step in the autonomous
                    stateMachine = StateMachine.DRIVE_TO_TARGET_0;
                    break;
                case DRIVE_TO_TARGET_0:
                    /*
                    drive the robot to the first target, the .driveTo() function will return true once
                    the robot has reached the target, and has been there for (holdTime) seconds.
                    Once driveTo returns true, it prints a telemetry line and moves the state machine forward.
                     */
                    if (wayfinder.driveTo(TARGET_0, 0.7, 0)){
                        telemetry.addLine("at position #0!");
                        stateMachine = StateMachine.DRIVE_TO_TARGET_1;
                    }
                    break;
                case DRIVE_TO_TARGET_1:
                    //drive to the second target
                    if (wayfinder.driveTo(TARGET_1, 0.7, 1)){
                        telemetry.addLine("at position #1!");
                        stateMachine = StateMachine.DRIVE_TO_TARGET_2;
                    }
                    break;
                case DRIVE_TO_TARGET_2:
                    if(wayfinder.driveTo(TARGET_2, 0.7, 3)){
                        telemetry.addLine("at position #2");
                        stateMachine = StateMachine.AT_TARGET;
                    }
                    break;
            }

            //nav calculates the power to set to each motor in a mecanum or tank drive. Use nav.getMotorPower to find that value.
            frontLeftDrive.setPower(wayfinder.getFrontLeftMotorOutput());
            frontRightDrive.setPower(wayfinder.getFrontRightMotorOutput());
            backLeftDrive.setPower(wayfinder.getBackLeftMotorOutput());
            backRightDrive.setPower(wayfinder.getBackRightMotorOutput());

            telemetry.addData("current state:",stateMachine);

            Pose2D pos = pinpoint.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);
            telemetry.update();

        }
    }

    public void initializeMotors(){
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void initializePinpoint(){
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");
        pinpoint.setOffsets(0, 0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);

        pinpoint.resetPosAndIMU();

    }


}