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
            .rightFrontMotorName("cm0")
            .rightRearMotorName("cm3")
            .leftRearMotorName("cm2")
            .leftFrontMotorName("cm1")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(89.49)
            .yVelocity(71.86087684931718);
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(10.8)
            .forwardZeroPowerAcceleration(-36.34581303569891)
            .lateralZeroPowerAcceleration(-54.50749004573832)
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(3,0,0.04, 0))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.051, 0.0, 0.003, 0.0))
            .headingPIDFCoefficients(new PIDFCoefficients(1, 0.0, 0.01, 0.0))
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryDrivePIDF(true)
            .useSecondaryHeadingPIDF(true);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-2)
            .strafePodX(-3.5)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pp")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
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