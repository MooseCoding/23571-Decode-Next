package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Far12(val all: Alliance) {



    var startPoint = Pose(89.500, 8.0)
    var cP1 = Pose(87.304, 38.208)
    var row1 = Pose(110.0, 35.500)
    var row1End = Pose(129.0, 35.500)
    var shoot = Pose(93.0, 18.0)
    var hPIntake = Pose(133.0, 10.500)
    var row2Start = Pose(88.0, 20.0)
    var cP2 = Pose(87.304, 60.394)
    var row2End = Pose(110.0, 59.500)
    var rampIntake = Pose(130.0, 63.0)

    var row3 = Pose(110.0, 83.5, 0.0)
    var row3End = Pose(129.0, 83.5, 0.0)

    var park = Pose(102.0,13.0)

    var cyclePose = Pose(130.0, 21.35, 30/180.0 * PI)

    fun flipPose(p: Pose): Pose {
        return Pose(144 - p.x, p.y)
    }

    var ToRow1 = follower.pathBuilder()
    .addPath(
    BezierCurve(
    follower.pose,
    cP1,
    row1
    )
    )
    .setLinearHeadingInterpolation(follower.heading, Math.toRadians(0.0))
    .build()

    var Row1Intake = follower.pathBuilder()
    .addPath(
    BezierLine(
    follower.pose,
    row1End
    )
    )
    .setConstantHeadingInterpolation(Math.toRadians(0.0))
    .build()

    var ShootRow1 = follower.pathBuilder()
    .addPath(
    BezierLine(
    follower.pose,
    shoot
    )
    )
    .setLinearHeadingInterpolation(follower.heading, Math.toRadians(355.0))
    .build()

    var HumanPlayerIntake = follower.pathBuilder()
    .addPath(
    BezierLine(
    follower.pose,
    hPIntake
    )
    )
    .setConstantHeadingInterpolation(Math.toRadians(355.0))
    .build()

    var HumanPlayerShoot = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                shoot
            )
        )
        .setLinearHeadingInterpolation(follower.heading, PI/2)
        .build()

    var ToRow2 = follower.pathBuilder()
    .addPath(
    BezierCurve(
    follower.pose,
        cP2,
    row2Start
    )
    )
    .setLinearHeadingInterpolation(follower.heading, Math.toRadians(70.0))
    .build()

    var Row2Intake = follower.pathBuilder()
    .addPath(
        BezierLine(
    follower.pose,
    row2End
    )
    )
    .setLinearHeadingInterpolation(follower.heading, Math.toRadians(0.0))
    .build()

    var RampToShoot = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                shoot
            )
        )
        .setLinearHeadingInterpolation(follower.heading, Math.toRadians(70.0))
        .build()

    var cycle = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                cyclePose
            )
        )
        .setLinearHeadingInterpolation(follower.heading, 30.0/180.0*PI)
        .build()

    var path10 = follower.pathBuilder()
    .addPath(
    BezierLine(
    follower.pose,
    rampIntake
    )
    )
    .setLinearHeadingInterpolation(follower.heading, Math.toRadians(355.0))
    .build()

    var Park = follower.pathBuilder()
        .addPath(
            BezierLine(
                follower.pose,
                park
            )
        )
        .setConstantHeadingInterpolation(follower.heading)
        .build()

    init {
        if (all == Alliance.BLUE) {
            startPoint = flipPose(startPoint)
            cP1 = flipPose(cP1)
            row1 = flipPose(row1)
            row1End = flipPose(row1End)
            shoot = flipPose(shoot)
            hPIntake = flipPose(hPIntake)
            row2Start = flipPose(row2Start)
            cP2 = flipPose(cP2)
            row2End = flipPose(row2End)
            rampIntake = flipPose(rampIntake)
            row3 = flipPose(row3)
            row3End = flipPose(row3End)
            park = flipPose(park)
        }
    }
}

public class Close12 {

}
