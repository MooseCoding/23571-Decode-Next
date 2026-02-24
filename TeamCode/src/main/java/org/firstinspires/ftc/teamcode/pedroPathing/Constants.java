package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.helpers.controllers.FusionLocalizer;

public class Constants {
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("fR")
            .rightRearMotorName("bR")
            .leftRearMotorName("bL")
            .leftFrontMotorName("fL")
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .xVelocity(67.76)
            .yVelocity(49);
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(12.29)
            .forwardZeroPowerAcceleration(-34.089)
            .lateralZeroPowerAcceleration(-74.2867)
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(3,0,0.04, 0))
            .translationalPIDFCoefficients(new PIDFCoefficients(0.051, 0.0, 0.003, 0.0))
            .headingPIDFCoefficients(new PIDFCoefficients(1, 0.0, 0.01, 0.0))
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryDrivePIDF(true)
            .useSecondaryHeadingPIDF(true)
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.1, 0.044677792115235176,0.002249232044246721));

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-15)
            .strafePodX(-2)
            .distanceUnit(DistanceUnit.CM)
            .hardwareMapName("pp")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .pinpointLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();
    }}