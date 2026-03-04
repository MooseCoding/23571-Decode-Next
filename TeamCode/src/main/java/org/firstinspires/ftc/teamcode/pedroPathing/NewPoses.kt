package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.Path
import com.pedropathing.paths.PathChain
import dev.nextftc.core.units.rad
import kotlin.math.PI

class NewPoses {
    // Close Poses
    var closeStart: Pose = Pose(32.0, 135.0, -PI/2)
    var row2Intake: Pose =Pose(14.5, 60.0, PI)
    var controlPointRow2: Pose = Pose(82.9, 53.6)

    var turn: Pose = Pose(12.5, 60.0, 3*PI/2)

    var closeShoot: Pose = Pose(56.5, 80.0) // Tangential
    var gateIntake: Pose = Pose(27.0, 72.5, 192.0)
    var gateIntakeSweep: Pose = Pose(12.65, 52.0,110.0)
    var gateControlPoint: Pose = Pose(20.2, 43.1)

    var intakeRow1: Pose = Pose(23.0, 84.0, PI)
    var intakeControl: Pose = Pose(55.1, 86.1)

    var finalShoot: Pose = Pose(50.59, 115.68) // Tangential

    // Far Poses
    var farStart: Pose = Pose(56.0, 8.0, PI/2)
    var intakeRow3: Pose = Pose(14.0, 35.64) // Tangential
    var controlPointRow3: Pose = Pose(61.2, 38.8)

    var farShotPose: Pose = Pose(50.0,12.0) // Tangential
    var humanPlayerIntake: Pose = Pose(11.0, 12.65) // Tangential

    var partialHuman = Pose(20.0, 15.0)

    var lastHuman = Pose(11.0, 9.65)

    var farGateIntake: Pose = Pose(13.0, 33.5)
    var farGateControlPoint: Pose = Pose(17.2, 8.3)

    fun flipPose() {
        closeStart = closeStart.mirror()
        row2Intake = row2Intake.mirror()
        controlPointRow2 = controlPointRow2.mirror()
        turn = turn.mirror()
        closeShoot = closeShoot.mirror()
        gateIntake = gateIntake.mirror()
        gateIntakeSweep = gateIntakeSweep.mirror()
        gateControlPoint = gateControlPoint.mirror()
        intakeRow1 = intakeRow1.mirror()
        intakeControl = intakeControl.mirror()
        finalShoot = finalShoot.mirror()
        farStart = farStart.mirror()
        intakeRow3 = intakeRow3.mirror()
        controlPointRow3 = controlPointRow3.mirror()
        farShotPose = farShotPose.mirror()
        humanPlayerIntake = humanPlayerIntake.mirror()
        farGateIntake = farGateIntake.mirror()
        farGateControlPoint = farGateControlPoint.mirror()
    }

    lateinit var intake2: PathChain
    lateinit var gateIntakeChain: PathChain
    lateinit var gateIntakeToShoot: PathChain
    lateinit var intake1: PathChain

    fun setupClose(follower: Follower) {
        intake2 = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    closeStart,
                    controlPointRow2,
                    row2Intake
                )
            )
            .addPath(
                BezierPoint(
                    row2Intake
                )
            ).setLinearHeadingInterpolation(closeStart.heading, row2Intake.heading)
            .addPath(
                BezierLine(
                    turn,
                    closeShoot
                )
            ).setReversed()
            .build()
        gateIntakeChain = follower.pathBuilder()
            .addPath(
                BezierLine(
                    closeShoot,
                    gateIntake
                )
            ).setConstantHeadingInterpolation(gateIntake.heading)
            .addPath(
                BezierCurve(
                    gateIntake,
                    gateControlPoint,
                    gateIntakeSweep
                )
            ).setLinearHeadingInterpolation(gateIntake.heading, gateIntakeSweep.heading)
            .build()
        gateIntakeToShoot = follower.pathBuilder()
            .addPath(
                BezierLine(
                    gateIntakeSweep,
                    closeShoot
                )
            ).setTangentHeadingInterpolation()
            .build()
        intake1 = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    closeShoot,
                    intakeControl,
                    intakeRow1
                )
            )
            .addPath(
                BezierLine(
                    intakeRow1,
                    finalShoot
                )
            )
            .build()
    }

    lateinit var farIntake: PathChain
    lateinit var humanPlayer: PathChain
    lateinit var humanPlayerShoot: PathChain
    lateinit var cycleHP: PathChain
    lateinit var rampIntake: PathChain
    lateinit var rampShoot: PathChain

    fun setupFar(follower: Follower) {
        farIntake = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    farStart,
                    controlPointRow3,
                    intakeRow3
                )
            )
            .addPath(
                BezierLine(
                    intakeRow3,
                    farShotPose
                )
            ).setReversed()
            .build()
        humanPlayer = follower.pathBuilder()
            .addPath(
                BezierLine(
                    farShotPose,
                    humanPlayerIntake
                )
            )
            .build()
        cycleHP = follower.pathBuilder()
            .addPath(
                BezierLine(
                    humanPlayerIntake,
                    partialHuman
                )
            )
            .addPath(
                BezierLine(
                    partialHuman,
                    lastHuman
                )
            )
            .build()
        humanPlayerShoot = follower.pathBuilder()
            .addPath(
                BezierLine(
                    lastHuman,
                    farShotPose
                )
            ).setReversed()
            .build()
        rampIntake = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    farShotPose,
                    farGateControlPoint,
                    farGateIntake
                )
            )
            .build()
        rampShoot = follower.pathBuilder()
            .addPath(
                BezierLine(
                    farGateIntake,
                    farShotPose
                )
            ).setReversed()
            .build()
    }
}