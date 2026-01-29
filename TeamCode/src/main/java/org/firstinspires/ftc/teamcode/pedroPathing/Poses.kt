package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Far12(val all: Alliance) {

    // ---------------- POSES (ALL HEADINGS LIVE HERE) ----------------

    var startPoint = Pose(89.500, 8.0, PI / 2)

    var cP1 = Pose(87.304, 38.208)
    var row1 = Pose(110.0, 35.500, 0.0)
    var row1End = Pose(129.0, 35.500, 0.0)

    var shoot = Pose(93.0, 18.0, Math.toRadians(355.0))
    var hPIntake = Pose(133.0, 10.500, Math.toRadians(355.0))

    var row2Start = Pose(88.0, 20.0, Math.toRadians(70.0))
    var cP2 = Pose(87.304, 60.394)
    var row2End = Pose(110.0, 59.500, 0.0)

    var rampIntake = Pose(130.0, 63.0, Math.toRadians(355.0))

    var row3 = Pose(110.0, 83.5, 0.0)
    var row3End = Pose(129.0, 83.5, 0.0)

    var park = Pose(102.0, 13.0, 0.0)

    var cyclePose = Pose(130.0, 21.35, 30.0 / 180.0 * PI)

    var ToRow1 = follower.pathBuilder()
        .addPath(
            BezierCurve(
                follower.pose,
                cP1,
                row1
            )
        )
        .setLinearHeadingInterpolation(follower.heading, row1.heading)
        .build()

    var Row1Intake = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                row1End
            )
        )
        .setConstantHeadingInterpolation(row1End.heading)
        .build()

    var ShootRow1 = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                shoot
            )
        )
        .setLinearHeadingInterpolation(follower.heading, shoot.heading)
        .build()

    var HumanPlayerIntake = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                hPIntake
            )
        )
        .setConstantHeadingInterpolation(hPIntake.heading)
        .build()

    var HumanPlayerShoot = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                shoot
            )
        )
        .setLinearHeadingInterpolation(follower.heading, shoot.heading)
        .build()

    var ToRow2 = follower.pathBuilder()
        .addPath(
            BezierCurve(
                follower.pose,
                cP2,
                row2Start
            )
        )
        .setLinearHeadingInterpolation(follower.heading, row2Start.heading)
        .build()

    var Row2Intake = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                row2End
            )
        )
        .setLinearHeadingInterpolation(follower.heading, row2End.heading)
        .build()

    var RampToShoot = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                shoot
            )
        )
        .setLinearHeadingInterpolation(follower.heading, shoot.heading)
        .build()

    var cycle = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                cyclePose
            )
        )
        .setLinearHeadingInterpolation(follower.heading, cyclePose.heading)
        .build()

    var path10 = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                rampIntake
            )
        )
        .setLinearHeadingInterpolation(follower.heading, rampIntake.heading)
        .build()

    var Park = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                park
            )
        )
        .setConstantHeadingInterpolation(park.heading)
        .build()

    // ---------------- ALLIANCE FLIP ----------------

    init {
        if (all == Alliance.BLUE) {
            startPoint = startPoint.mirror()
            cP1 = cP1.mirror()
            row1 = row1.mirror()
            row1End = row1End.mirror()
            shoot = shoot.mirror()
            hPIntake = hPIntake.mirror()
            row2Start = row2Start.mirror()
            cP2 = cP2.mirror()
            row2End = row2End.mirror()
            rampIntake = rampIntake.mirror()
            row3 = row3.mirror()
            row3End = row3End.mirror()
            park = park.mirror()
            cyclePose = cyclePose.mirror()
        }
    }
}
