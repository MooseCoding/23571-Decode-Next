package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Far12(val all: Alliance) {

    // ---------------- POSES (ALL HEADINGS LIVE HERE) ----------------

    var startPoint = Pose(89.500, 8.0, PI / 2)

    var cP1 = Pose(87.304, 38.208)
    var row1 = Pose(110.0, 35.500, 0.0)
    var row1End = Pose(129.0, 35.500, 0.0)

    var shoot = Pose(93.0, 18.0, Math.toRadians(80.0))
    var hPIntake = Pose(133.0, 10.500, Math.toRadians(355.0))

    var row2Start = Pose(102.0, 59.5, 0.0)
    var cP2 = Pose(87.304, 60.394)
    var row2End = Pose(129.0, 59.500, 0.0)

    var rampIntake = Pose(130.0, 63.0, Math.toRadians(355.0))

    var row3 = Pose(110.0, 83.5, 0.0)
    var row3End = Pose(129.0, 83.5, 0.0)

    var park = Pose(102.0, 13.0, 0.0)

    var cyclePose = Pose(130.0, 21.35, 30.0 / 180.0 * PI)

    var farCycleShoot = Pose(shoot.x, shoot.y,20.0/180.0*PI)

    var p10 = Pose(89.5, 18.0, PI/2)

    lateinit var ToRow1: PathChain
    lateinit var Row1Intake: PathChain
    lateinit var Row2Intake: PathChain
    lateinit var ShootRow1: PathChain
    lateinit var PullOut: PathChain
    lateinit var HumanPlayerIntake: PathChain
    val Park: PathChain by lazy {
        follower.pathBuilder()
            .addPath(
                BezierLine(
                    follower.pose,
                    park
                )
            )
            .setConstantHeadingInterpolation(park.heading)
            .build()
    }
    lateinit var ToRow2: PathChain
    lateinit var HumanPlayerShoot: PathChain
    lateinit var RampToShoot: PathChain
    lateinit var path10: PathChain
    lateinit var cycle: PathChain

    lateinit var p1: PathChain

    lateinit var ShootToRow1: PathChain

    lateinit var CycleShoot: PathChain

    // ---------------- ALLIANCE FLIP ----------------

    fun init() {
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
            p10 = p10.mirror()
            farCycleShoot = farCycleShoot.mirror()

            ToRow1 = follower.pathBuilder()
                .addPath(
                    BezierCurve(
                        startPoint,
                        cP1,
                        row1
                    )
                )
                .setLinearHeadingInterpolation(startPoint.heading, row1.heading)
                .build()

            ShootToRow1 = follower.pathBuilder()
                .addPath(
                    BezierCurve(
                        shoot,
                        cP1,
                        row1
                    )
                )
                .setLinearHeadingInterpolation(shoot.heading, row1.heading)
                .build()



            Row1Intake = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        row1,
                        row1End
                    )
                )
                .setConstantHeadingInterpolation(row1End.heading)
                .build()

            PullOut = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        row2End,
                        row2Start
                    )
                )
                .setConstantHeadingInterpolation(row2Start.heading)
                .build()

            ShootRow1 = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        row1End,
                        shoot
                    )
                )
                .setLinearHeadingInterpolation(row1End.heading, shoot.heading)
                .build()

            HumanPlayerIntake = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        shoot,
                        hPIntake
                    )
                )
                .setConstantHeadingInterpolation(hPIntake.heading)
                .build()

            HumanPlayerShoot = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        hPIntake,
                        shoot
                    )
                )
                .setLinearHeadingInterpolation(hPIntake.heading, farCycleShoot.heading)
                .build()

            ToRow2 = follower.pathBuilder()
                .addPath(
                    BezierCurve(
                        startPoint,
                        cP2,
                        row2Start
                    )
                )
                .setLinearHeadingInterpolation(startPoint.heading, row2Start.heading)
                .build()

            Row2Intake = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        row2Start,
                        row2End
                    )
                )
                .setLinearHeadingInterpolation(row2Start.heading, row2End.heading)
                .build()

            RampToShoot = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        row2Start,
                        shoot
                    )
                )
                .setLinearHeadingInterpolation(row2Start.heading, shoot.heading)
                .build()

            cycle = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        farCycleShoot,
                        cyclePose
                    )
                )
                .setLinearHeadingInterpolation(farCycleShoot.heading, cyclePose.heading)
                .build()

            CycleShoot = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        cyclePose,
                        farCycleShoot
                    )
                ).setLinearHeadingInterpolation(cyclePose.heading, farCycleShoot.heading)
                .build()

            path10 = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        follower.pose,
                        rampIntake
                    )
                )
                .setLinearHeadingInterpolation(follower.heading, rampIntake.heading)
                .build()

            p1 = follower.pathBuilder()
                .addPath(
                    BezierLine(
                        follower.pose,
                        p10,
                    )
                )
                .setConstantHeadingInterpolation(follower.heading)
                .build()
        }
    }
}
