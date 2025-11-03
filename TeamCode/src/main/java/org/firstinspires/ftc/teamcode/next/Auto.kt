package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.pedroPathing.Constants


@Autonomous
class Auto() : NextFTCOpMode() {
    val telem = JoinedTelemetry(telemetry, PanelsTelemetry.ftcTelemetry)
    val fL = MotorEx("frontLeft").reversed()
    val fR = MotorEx("frontRight")
    val bL = MotorEx("backLeft").reversed()
    val bR = MotorEx("backRight")
    val follower = Constants.createFollower(hardwareMap)

    var builder: PathBuilder = PathBuilder(follower)

    var line1: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(25.684, 125.895),
                Pose(85.053, 83.158),
                Pose(38.947, 83.368)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line2: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 83.368), Pose(23.579, 83.579)))
        .setTangentHeadingInterpolation()
        .build()

    var line3: PathChain = builder
        .addPath(BezierLine(Pose(23.579, 83.579), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line4: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(49.684, 97.684),
                Pose(66.947, 59.579),
                Pose(38.947, 60.211)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line5: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 60.211), Pose(14.316, 60.421)))
        .setTangentHeadingInterpolation()
        .build()

    var line6: PathChain = builder
        .addPath(BezierLine(Pose(14.316, 60.421), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line7: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(49.684, 97.684),
                Pose(74.737, 35.579),
                Pose(38.947, 35.368)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line8: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 35.368), Pose(17.895, 35.368)))
        .setTangentHeadingInterpolation()
        .build()

    var line9: PathChain = builder
        .addPath(BezierLine(Pose(17.895, 35.368), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line10: PathChain = builder
        .addPath(BezierLine(Pose(49.684, 97.684), Pose(49.684, 71.789)))
        .setTangentHeadingInterpolation()
        .build()

    override fun onInit() {
        super.onInit()
    }

    override fun onUpdate() {

    }
}