/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.wayfinder;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import dalvik.system.DelegateLastClassLoader;


@TeleOp(name="Concept Wayfinder Tester", group="Pinpoint")
//@Disabled
public class ConceptWayfinderTester extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private GoBildaPinpointDriver pinpoint = null; // Declare OpMode member for the Odometry Computer

    private ElapsedTime driveTimer = new ElapsedTime();

    private final double TOLERANCE = 50;

    private double frontLeftMotorOutput = 0;
    private double frontRightMotorOutput = 0;
    private double backLeftMotorOutput = 0;
    private double backRightMotorOutput = 0;

    public enum StateMachine{
        WAIT_FOR_START,
        TEST_X,
        X_CORRECT,
        TEST_Y,
        Y_CORRECT,
        X_MISMATCH,
        Y_MISMATCH,
        CORRECT,
    }
    StateMachine stateMachine = StateMachine.WAIT_FOR_START;

    @Override
    public void runOpMode() {

        initializeMotors();

        initializePinpoint();

        // Wait for the game to start (driver presses START)
        telemetry.addLine("Initialized: Press the Play button to continue");
        telemetry.update();

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            switch (stateMachine){
                case WAIT_FOR_START:
                    if(gamepad1.aWasPressed()){
                        pinpoint.resetPosAndIMU();
                        sleep(500);
                        driveTimer.reset();
                        stateMachine = StateMachine.TEST_X;
                    }
                    break;
                case TEST_X:
                    if(driveTimer.seconds() < 0.5){
                        calculateMecanumOutput(0.5,0,0);
                    } else {
                        calculateMecanumOutput(0,0,0);
                        if(pinpoint.getPosX(DistanceUnit.MM) > TOLERANCE){
                            stateMachine = StateMachine.X_CORRECT;
                        } else {
                            stateMachine = StateMachine.X_MISMATCH;
                        }
                    }
                    break;
                case X_CORRECT:
                    if(gamepad1.aWasPressed()){
                        driveTimer.reset();
                        stateMachine = StateMachine.TEST_Y;
                    }
                    if(gamepad1.xWasPressed()){
                        stateMachine = StateMachine.X_MISMATCH;
                    }
                    break;
                case TEST_Y:
                    if(driveTimer.seconds() < 0.5){
                        calculateMecanumOutput(0,0.5,0);
                    } else {
                        calculateMecanumOutput(0,0,0);
                        if(pinpoint.getPosY(DistanceUnit.MM) > TOLERANCE){
                            stateMachine = StateMachine.Y_CORRECT;
                        } else {
                            stateMachine = StateMachine.Y_MISMATCH;
                        }
                    }
                    break;
                case Y_CORRECT:
                    if(gamepad1.aWasPressed()){
                        stateMachine = StateMachine.CORRECT;
                    }
                    if(gamepad1.xWasPressed()){
                        stateMachine = StateMachine.Y_MISMATCH;
                    }
                    break;
                case CORRECT:
                case X_MISMATCH:
                case Y_MISMATCH:
                    if(gamepad1.bWasPressed()){
                        stateMachine = StateMachine.WAIT_FOR_START;
                    }
                    break;
            }

            frontLeftDrive.setPower(frontLeftMotorOutput);
            frontRightDrive.setPower(frontRightMotorOutput);
            backLeftDrive.setPower(backLeftMotorOutput);
            backRightDrive.setPower(backRightMotorOutput);

            pinpoint.update();

            telemetry.addLine(telemetry(stateMachine));
            telemetry.addLine("");

            telemetry.addData("X in MM", pinpoint.getPosX(DistanceUnit.MM));
            telemetry.addData("Y in MM", pinpoint.getPosY(DistanceUnit.MM));
            telemetry.addData("Heading in Degrees", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.update();
        }
    }

    private String telemetry(StateMachine stateMachine){
        String output = "";
        switch (stateMachine){
            case WAIT_FOR_START:
                output = "Welcome to the configuration tool for Wayfinder." + System.lineSeparator() +
                        "This program will move your robot and read the position reported by " +
                        "the Pinpoint to confirm that it is configured correctly. " + System.lineSeparator() +
                        System.lineSeparator() +
                        "When you are ready to proceed, press A on the gamepad.";
                break;
            case TEST_X:
            case TEST_Y:
                output = "";
                break;
            case X_CORRECT:
                output = "If the robot drove forward, please press the A button to continue." +
                        System.lineSeparator() + "If the robot did not drive forward, press X";
                break;
            case Y_CORRECT:
                output = "If the robot drove left, please press the A button to continue." +
                        System.lineSeparator() + "If the robot did not drive left, press X";
                break;
            case X_MISMATCH:
                output = "The Pinpoint did not observe an increase in X" + System.lineSeparator() +
                        "If the robot drove forward, correct this problem by reversing the X " +
                        "encoder on the Pinpoint." + System.lineSeparator() + System.lineSeparator() +
                        "If the robot drove backwards, please confirm that each drive motor aligns " +
                        "with the config file. If that is correct, you may need to reverse the " +
                        "right side motors instead of the left side.";
                break;
            case Y_MISMATCH:
                output = "The Pinpoint did not observe an increase in Y" + System.lineSeparator() +
                        "if the robot drove left, correct this problem by reversing the Y " +
                        "encoder on the Pinpoint." + System.lineSeparator() + System.lineSeparator() +
                        "If the robot drove in any other direction, please confirm that each drive" +
                        "motor aligns with the config file.";
                break;
            case CORRECT:
                output = "You've completed the setup process for the Wayfinder! " +
                        System.lineSeparator() + "If you needed to reverse encoder directions " +
                        "for this test to complete, carry those modifications over " +
                        "to your primary OpMode.";
                break;
        }
        return output;
    }

    /**
     * This is a standard mecanum mix function.
     * @param forward -1 to 1, requested forward drive power.
     * @param strafe -1 to 1, requested strafe drive power.
     * @param yaw -1 to 1, requested yaw (heading) drive power.
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
        pinpoint.setOffsets(-142.0, 120.0, DistanceUnit.MM); //these are tuned for 3110-0002-0001 Product Insight #1
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);

        pinpoint.resetPosAndIMU();
    }
}