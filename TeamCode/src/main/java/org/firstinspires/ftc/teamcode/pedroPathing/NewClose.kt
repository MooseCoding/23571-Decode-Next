package org.firstinspires.ftc.teamcode.pedroPathing

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.helpers.TelemetryImplUpstreamSubmission
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.wayfinder.Pose
import kotlin.math.PI
import kotlin.math.nextUp
import kotlin.time.Duration.Companion.seconds

@Autonomous(preselectTeleOp = "TeleOp")
class NewClose: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake, FlywheelLight, Light),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val poses:NewPoses = NewPoses()

    override fun onInit() {
        Turret.autoTurret = true
        Outtake.fullManual = true
    }

    val telemetry: TelemetryImplUpstreamSubmission by lazy { TelemetryImplUpstreamSubmission(this) }

    override fun onWaitForStart() {
        if (gamepad1.triangle) { alliance = Alliance.RED }
        if (gamepad1.circle) { alliance = Alliance.BLUE }

        telemetry.run {
            addData("Alliance ", alliance)
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)
            addData("Target", Outtake.targetPose)
            update()
        }

        when(alliance) {
            Alliance.RED -> {
                FlywheelLight.Red().schedule()
                Outtake.targetPose = com.pedropathing.geometry.Pose(142.0, 144.0)
            }
            Alliance.BLUE -> {
                FlywheelLight.Blue().schedule()
                Outtake.targetPose = com.pedropathing.geometry.Pose(-3.0, 144.0)
            }
        }
    }

    override fun onStartButtonPressed() {
        if(alliance == Alliance.RED) {
            poses.flipPose()
        }
        poses.setupClose(follower) 

        follower.setStartingPose(poses.newStart)

        Outtake.fullManual = true
        Flywheels.targetVelocity = 1400.0
        Hood.hoodPosition = 0.65

        SequentialGroupLocal(
            InstantCommand {
                Flywheels.targetVelocity = 1400.0
                Hood.hoodPosition = 0.68
            } ,
            Flywheels.spin(),
            FollowPath(poses.shootStart),
            Delay(3.0.seconds),
            Outtake.shoot(),
            Intake.runIntake(),
            InstantCommand {
                Flywheels.targetVelocity = 1400.0
                Hood.hoodPosition = 0.68
            } ,
            FollowPath(poses.shootIntake),
            Intake.runIntake(),
            Outtake.shoot(),
            Intake.runIntake(),

            InstantCommand {
                Flywheels.targetVelocity = 1400.0
                Hood.hoodPosition = 0.68
            } ,

            FollowPath(poses.gateIntakeChain),
            Intake.runIntake(),

            Delay(0.6.seconds),
            FollowPath(poses.gateIntakeToShoot),

            Outtake.shoot(),
            FollowPath(poses.gateIntakeChain),
            Intake.runIntake(),

            Delay(0.6.seconds),

            FollowPath(poses.gateIntakeToShoot),

            Outtake.shoot(), // 9 ball
//            FollowPath(poses.gateIntakeChain),
//            Intake.runIntake(),
//
//            Delay(0.6.seconds),
//
//            FollowPath(poses.gateIntakeToShoot),
//
//            Outtake.shoot(), // 15 balls
            Intake.runIntake(),

            InstantCommand {
                Flywheels.targetVelocity = 1320.0
                Hood.hoodPosition = 0.75
            },
            FollowPath(poses.intake1),
            Outtake.shoot() // 15 balls
        ).schedule()
    }

    override fun onStop() {
        val p = PedroComponent.follower.pose
        DriveTrain.currentX = p.x
        DriveTrain.currentY = p.y
        DriveTrain.currentHeading = p.heading
    }

    override fun onUpdate() {
        telemetry.run {
            addData("bleh", poses.gateIntakeSweep.heading * 180/ PI)
            update()
        }
    }
}