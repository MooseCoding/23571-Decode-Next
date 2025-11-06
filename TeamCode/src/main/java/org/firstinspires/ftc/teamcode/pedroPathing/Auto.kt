package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathBuilder
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import kotlin.time.Duration.Companion.seconds


@Autonomous
class Auto() : NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Intake, Outtake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
            PedroComponent(Constants::createFollower)
        )
    }
    val telem = JoinedTelemetry(telemetry, PanelsTelemetry.ftcTelemetry)
    private var pathTimer: Timer? = null
    private var opmodeTimer: Timer? = null
    private var pathState = 0
    var startingPose = Pose() // Starting pose, change to depend on the drivetrain

    var Path1 = follower // Move to Shooting Position with Preloads
        .pathBuilder()
        .addPath(
            BezierLine(Pose(24.541, 126.802), Pose(40.000, 110.559))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path2 = follower // Head to pickup 2 Purple Balls from line closest to classifier
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(40.000, 110.559),
                Pose(66.507, 81.982),
                Pose(39.692, 83.807)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path3 = follower // Forwards to intake the 2 purple balls
        .pathBuilder()
        .addPath(
            BezierLine(Pose(39.692, 83.807), Pose(14.168, 83.534))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path4 = follower // Head back to inside the loading zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(14.168, 83.534), Pose(56.071, 84.353))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path5 = follower // Head to grab 1 green and then 1 purple ball on the furthest line
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(56.071, 84.353),
                Pose(59.655, 34.258),
                Pose(40.000, 35.352)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path6 = follower // Forwards to intake the 1 Green then 1 Purple
        .pathBuilder()
        .addPath(
            BezierLine(Pose(40.000, 35.352), Pose(11.301, 35.625))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path7 = follower // Drive backwards to the tip of the far shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(11.301, 35.625), Pose(71.768, 20.337))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path8 = follower // Head to pickup 1 Purple then 1 Green Ball
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(71.768, 20.337),
                Pose(71.823, 60.364),
                Pose(40.000, 59.784)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path9 = follower // Forwards to intake 1 purple then 1 green ball
        .pathBuilder()
        .addPath(
            BezierLine(Pose(40.000, 59.784), Pose(11.574, 59.647))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path10 =  follower // Move backwards to the tip of the closer shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(11.574, 59.647), Pose(72.859, 74.525))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path11 = follower // Move out of the shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(72.859, 74.525), Pose(51.623, 61.782))
        )
        .setTangentHeadingInterpolation()
        .build()

    private val pathSequence = SequentialGroup(
        FollowPath(Path1), // Move to Shooting Position with Preloads while shooting the preloads
        FollowPath(Path2), // Head to pickup 2 Purple Balls from line closest to classifier
        SequentialGroup(FollowPath(Path3), Intake.runIntake),  // Forwards to intake the 2 purple balls
        SequentialGroup(FollowPath(Path4), Intake.stopIntake), // Head back to inside the loading zone
        FollowPath(Path5),// Head to grab 1 green and then 1 purple ball on the furthest line
        SequentialGroup(FollowPath(Path6), Intake.runIntake), // Forwards to intake the 1 Green then 1 Purple
        SequentialGroup(FollowPath(Path7), Intake.stopIntake), // Drive backwards to the tip of the far shooting zone
        SequentialGroup(FollowPath(Path8)), // Head to pickup 1 Purple then 1 Green Ball
        SequentialGroup(FollowPath(Path9), Intake.runIntake), // Forwards to intake 1 purple then 1 green ball
        SequentialGroup(FollowPath(Path10), Intake.stopIntake), // Move backwards to the tip of the closer shooting zone
        SequentialGroup(FollowPath(Path11)) // Move out of the shooting zone
    )

    override fun onInit() {
        pathTimer = Timer()
        opmodeTimer = Timer()

        opmodeTimer!!.resetTimer()

        follower.setStartingPose(startingPose)
        follower.update()
    }

    override fun onStartButtonPressed() {
        pathSequence.schedule()
    }

    override fun onUpdate() {
        follower.update()

        telemetry.run {
            addData("Follower X", follower.pose.x)
            addData("Follower Y", follower.pose.y)
            addData("Follower Heading", follower.pose.heading)
            addLine()
            addData("Path State", pathState)
            addData("Path Timer", pathTimer!!.elapsedTimeSeconds)
        }
    }

    
}
