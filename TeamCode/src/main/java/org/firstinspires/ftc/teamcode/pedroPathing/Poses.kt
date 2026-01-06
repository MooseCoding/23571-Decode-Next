package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import dev.nextftc.core.commands.groups.ParallelGroup
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.io.path.Path
import kotlin.math.PI

public class Far12(val all: Alliance) {

    init {
        if (all == Alliance.BLUE) {
            startPoint = flipPose(startPoint)
            cP1 = flipPose(cP1)
            row1 = flipPose(row1)
            row1End = flipPose(row1End)
            shoot = flipPose(shoot)
            hPIntake = flipPose(hPIntake)
            row2Start = flipPose(row2Start)
            row2 = flipPose(row2)
            row2End = flipPose(row2End)
            rampIntake = flipPose(rampIntake)
            row3 = flipPose(row3)
            row3End = flipPose(row3End)
        }
    }

    companion object {
        var startPoint = Pose(89.500, 8.0)
        var cP1 = Pose(87.304, 38.208)
        var row1 = Pose(110.0, 35.500)
        var row1End = Pose(129.0, 35.500)
        var shoot = Pose(93.0, 18.0)
        var hPIntake = Pose(133.0, 10.500)
        var row2Start = Pose(88.0, 20.0)
        var row2 = Pose(87.304, 60.394)
        var row2End = Pose(110.0, 59.500)
        var rampIntake = Pose(130.0, 63.0)

        var row3 = Pose(110.0, 83.5, 0.0)
        var row3End = Pose(129.0, 83.5, 0.0)

        fun flipPose(p: Pose): Pose {
            return Pose(144 - p.x, p.y)
        }

        var path1 = follower.pathBuilder()
        .addPath(
        BezierCurve(
        follower.pose,
        cP1,
        row1
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(90.0), Math.toRadians(0.0))
        .build()

        var path2 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        row1End
        )
        )
        .setConstantHeadingInterpolation(Math.toRadians(0.0))
        .build()

        var path3 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        shoot
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(0.0), Math.toRadians(355.0))
        .build()

        var path4 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        hPIntake
        )
        )
        .setConstantHeadingInterpolation(Math.toRadians(355.0))
        .build()

        var path5 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        row2Start
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(355.0), Math.toRadians(70.0))
        .build()

        var path6 = follower.pathBuilder()
        .addPath(
        BezierCurve(
        follower.pose,
        row2,
        row2End
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(70.0), Math.toRadians(0.0))
        .build()

        var path7 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        row2End
        )
        )
        .setConstantHeadingInterpolation(Math.toRadians(0.0))
        .build()

        var path8 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        row2End
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(0.0), Math.toRadians(280.0))
        .build()

        var path9 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        row2Start
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(280.0), Math.toRadians(44.0))
        .build()

        var path10 = follower.pathBuilder()
        .addPath(
        BezierLine(
        follower.pose,
        rampIntake
        )
        )
        .setLinearHeadingInterpolation(Math.toRadians(44.0), Math.toRadians(355.0))
        .build()

    }
}

public class Close12 {

}
