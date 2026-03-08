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

    var newStart = Pose(32.0, 135.0, PI/2)

    var row2Intake: Pose =Pose(16.0, 60.0, PI)
    var controlPointRow2: Pose = Pose(82.9, 53.6)

    var turn: Pose = Pose(12.5, 60.0, 3*PI/2)

    var closeShoot: Pose = Pose(55.5, 78.0, 150.0*PI/180 ) // Tangential
    var gateIntake: Pose = Pose(25.0, 72.5, 192.0 * PI/180)
    var gateIntakeSweep: Pose = Pose(12.65, 50.0,110.0 * PI/180)
    var gateControlPoint: Pose = Pose(20.2, 43.1)

    var intakeRow1: Pose = Pose(23.0, 84.0, PI)
    var intakeControl: Pose = Pose(55.1, 86.1)

    var finalShoot: Pose = Pose(50.59, 115.68) // Tangential

    // Far Poses
    var farStart: Pose = Pose(56.0, 8.0, PI/2)
    var intakeRow3: Pose = Pose(15.0, 31.84) // Tangential
    var controlPointRow3: Pose = Pose(61.2, 38.8)

    var farShotPose: Pose = Pose(50.0,12.0) // Tangential
    var humanPlayerIntake: Pose = Pose(12.0, 12.65) // Tangential

    var partialHuman = Pose(20.0, 15.0)

    var lastHuman = Pose(14.0, 13.65)

    var farGateIntake: Pose = Pose(12.0, 33.5)
    var farGateControlPoint: Pose = Pose(17.2, 8.3)

    fun flipPose() {
        closeStart = closeStart.mirror(141.5)
        newStart = newStart.mirror(141.5)
        row2Intake = row2Intake.mirror(141.5)
        controlPointRow2 = controlPointRow2.mirror(141.5)
        turn = turn.mirror(141.5)
        closeShoot = closeShoot.mirror(141.5)
        gateIntake = gateIntake.mirror(141.5)
        gateIntakeSweep = gateIntakeSweep.mirror(141.5)
        gateControlPoint = gateControlPoint.mirror(141.5)
        intakeRow1 = intakeRow1.mirror(141.5)
        intakeControl = intakeControl.mirror(141.5)
        finalShoot = finalShoot.mirror(141.5)
        farStart = farStart.mirror(141.5)
        intakeRow3 = intakeRow3.mirror(141.5)
        controlPointRow3 = controlPointRow3.mirror(141.5)
        farShotPose = farShotPose.mirror(141.5)
        humanPlayerIntake = humanPlayerIntake.mirror(141.5)
        farGateIntake = farGateIntake.mirror(141.5)
        farGateControlPoint = farGateControlPoint.mirror(141.5)
        partialHuman = partialHuman.mirror(141.5)
        lastHuman = lastHuman.mirror(141.5)
    }

    lateinit var intake2: PathChain
    lateinit var shootStart: PathChain
    lateinit var shootIntake: PathChain
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
        shootStart = follower.pathBuilder()
            .addPath(
                BezierLine(
                    newStart,
                    closeShoot
                )
            )
            .setLinearHeadingInterpolation(newStart.heading, closeShoot.heading)
            .build()
        shootIntake = follower.pathBuilder()
            .addPath(
                BezierCurve(
                    closeShoot,
                    controlPointRow2,
                    row2Intake
                )
            ).setTangentHeadingInterpolation()
            .addPath(
                BezierLine(
                    row2Intake,
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
            )
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
    lateinit var humanPlayerDirect: PathChain

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
            ).setReversed()
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
        humanPlayerDirect = follower.pathBuilder()
            .addPath(
                BezierLine(
                    humanPlayerIntake,
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