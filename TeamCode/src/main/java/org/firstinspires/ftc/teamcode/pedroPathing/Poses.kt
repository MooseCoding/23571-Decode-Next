package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Close12(val all:Alliance) {
    var startPoint = Pose(34.0, 134.5,  3*PI/2)
    var row1 = Pose(43.5, 83.5, PI)
    var cp1 = Pose(65.1783, 89.8833)
    var row1End = Pose(14.5,83.5, PI)

    var row2 = Pose(43.5, 61.0, PI)
    var row2End = Pose(14.0, 61.0, PI)

    var rampShoot = Pose(43.5, 83.5, 160.0/180.0 * PI)
    var rampIntake = Pose(11.0, 58.5, 160.0/180.0 * PI)

    var row3 = Pose(43.5, 34.5, PI)
    var row3End = Pose(11.0, 34.5, PI)

    var farShoot = Pose(57.0, 18.67, 120.0/180.0 * PI)

    var park = Pose(52.3, 23.5, 120.0/180.0 * PI)

    lateinit var row1Path: Path
    lateinit var row1Intake: Path
    lateinit var row1Back: Path
    // lateinit var

    fun init() {

    }
}

public class Far12(val all: Alliance) {

    // ---------------- POSES (ALL HEADINGS LIVE HERE) ----------------

    var startPoint = Pose(89.500, 8.0, PI / 2)

    var cP1 = Pose(87.304, 38.208)
    var row1 = Pose(110.0, 40.500, 0.0)
    var row1End = Pose(144.0, 40.500, 0.0)

    var shoot = Pose(90.0, 18.0, Math.toRadians(80.0))
    var hPIntake = Pose(144.0, 18.500, Math.toRadians(20.0))

    var row2Start = Pose(102.0, 59.5, 0.0)
    var cP2 = Pose(87.304, 60.394)
    var row2End = Pose(135.0, 59.500, 0.0)

    var rampIntake = Pose(130.0, 63.0, Math.toRadians(355.0))

    var row3 = Pose(110.0, 83.5, 0.0)
    var row3End = Pose(135.0, 83.5, 0.0)

    var park = Pose(133.0, 20.0, 0.0)

    var cyclePose = Pose(130.0, 21.35, 80.0 / 180.0 * PI)

    var farCycleShoot = Pose(shoot.x, shoot.y,80.0/180.0*PI)

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
            startPoint = Pose(141.5-startPoint.x, startPoint.y, Math.PI/2)
            cP1 = Pose(141.5-cP1.x, cP1.y)
            row1 = Pose(141.5-row1.x-8.0, row1.y-20.0, Math.PI)
            row1End = Pose(20.0, row1End.y-20.0, Math.PI)
            shoot =  Pose(141.5-shoot.x-12.0, shoot.y-10.0, Math.PI-shoot.heading)
            hPIntake =  Pose(18.0, hPIntake.y-5.0, Math.PI-hPIntake.heading)
            row2Start =  Pose(141.5-row2Start.x, row2Start.y-5.0, Math.PI-row2Start.heading)
            cP2 = Pose(141.5-cP2.x, cP1.y)
            row2End = Pose(141.5-row2End.x, row2End.y-5.0, Math.PI)
            rampIntake = Pose(18.0, rampIntake.y-5.0, Math.PI-rampIntake.heading)
            row3 = Pose(141.5-row3.x, row3.y-5.0, -row3.heading)
            row3End = Pose(141.5-row3End.x, row3End.y-5.0, Math.PI-row3End.heading)
            park = Pose(141.5-park.x, park.y-5.0, Math.PI-park.heading)
            cyclePose = Pose(18.0, cyclePose.y-5.0, Math.PI-cyclePose.heading)
            p10 = Pose(141.5-p10.x, p10.y-5.0, -p10.heading)
            farCycleShoot = Pose(141.5-shoot.x-10.0, farCycleShoot.y-12.0, Math.PI-farCycleShoot.heading)
        }
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
