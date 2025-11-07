package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("frontRight")
            .rightRearMotorName("backRight")
            .leftRearMotorName("backLeft")
            .leftFrontMotorName("frontLeft")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(85.92951544814223)
            .yVelocity(44.462729626753195);
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(9.344)
            .forwardZeroPowerAcceleration(-46.72973878372108)
            .lateralZeroPowerAcceleration(-70.54184632845333)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0.0, 0.05, 0.0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.1, 0.0, 0.0, 0.0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.03, 0.0, 0.00003, 0.6,0.005))
            .useSecondaryTranslationalPIDF(false)
            .useSecondaryDrivePIDF(false)
            .useSecondaryHeadingPIDF(false);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-3)
            .strafePodX(-4)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pp")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .pinpointLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}