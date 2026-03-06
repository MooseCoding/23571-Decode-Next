package org.firstinspires.ftc.teamcode.next

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Gamepad
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.extensions.pedro.PedroDriverControlled
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.units.unittypes.TemperatureUnit
import org.firstinspires.ftc.teamcode.helpers.TelemetryImplUpstreamSubmission
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.pedroPathing.NewPoses
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

@TeleOp
class TeleOp: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent( DriveTrain, Intake, Outtake, Light, Sensor, Transfer)
        )
    }

    override fun onWaitForStart() {

    }

    override fun onInit() {
    }

    val fl = MotorEx("fL")// Yse
    val fr = MotorEx("fR")
    val bl = MotorEx("bL")
    val br = MotorEx("bR")// Yes

    var turret: Boolean = false

    var sens: Double = 1.0

    val poses = NewPoses()

    override fun onStartButtonPressed() {
        Outtake.tooMuch = false
        if(alliance == Alliance.RED) {
            poses.flipPose()
        }
        Flywheels.spin().schedule()
        PedroComponent.follower.pose = Pose(DriveTrain.currentX, DriveTrain.currentY, DriveTrain.currentHeading)
        // PedroComponent.follower.pose = poses.farStart

        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()

        // Shoot
        Gamepads.gamepad1.triangle whenBecomesTrue  {
            if(PedroComponent.follower.pose.y < 54.0) {
                Outtake.shootFar().schedule()
            }
            else {
                Outtake.shoot().schedule()
            }
        }

        Gamepads.gamepad2.triangle whenBecomesTrue {
            if (Flywheels.spinSlow) {
                Flywheels.spin().schedule()
            } else {
                Flywheels.stop().schedule()
            }
        }

        Gamepads.gamepad1.circle whenBecomesTrue {
            Turret.autoTurret = !Turret.autoTurret
            if(!Turret.autoTurret) {
                Turret.goToYaw(0.0)
            }
        }

        Gamepads.gamepad2.leftBumper whenBecomesTrue Turret.spinLeft()
        Gamepads.gamepad2.rightBumper whenBecomesTrue Turret.spinRight()

        Gamepads.gamepad2.dpadUp whenBecomesTrue {
            Outtake.hH -= 0.1
        }
        Gamepads.gamepad2.dpadDown whenBecomesTrue {
            Outtake.hH += 0.1
        }

        Gamepads.gamepad2.dpadLeft whenBecomesTrue {
            Outtake.fH -= 50
        }
        Gamepads.gamepad2.dpadRight whenBecomesTrue {
            Outtake.fH += 50
        }

        Gamepads.gamepad1.options whenBecomesTrue InstantCommand {
            when(DriveTrain.alliance) {
                Alliance.BLUE -> {
                    PedroComponent.follower.pose = Pose(8.0, 8.0, PI/2)
                }
                Alliance.RED -> {
                    PedroComponent.follower.pose = Pose(144-8.0, 8.0, PI/2)
                }
            }
        }

        Gamepads.gamepad1.share whenBecomesTrue InstantCommand {
            DriveTrain.alliance = when(DriveTrain.alliance) {
                Alliance.BLUE -> {
                    Alliance.RED
                }
                Alliance.RED -> {
                    Alliance.BLUE
                }
            }
        }


        // Set Close
        Gamepads.gamepad1.dpadUp.whenBecomesTrue {
            Outtake.fullManual = true
            Hood.hoodPosition = Outtake.closeHood + Outtake.hH
            Flywheels.targetVelocity = Outtake.closeVelocity + Outtake.fH
        }

        Gamepads.gamepad1.dpadDown.whenBecomesTrue {
            Outtake.fullManual = true
            Hood.hoodPosition = Outtake.farHood + Outtake.hH
            Flywheels.targetVelocity = Outtake.farVelocity + Outtake.fH
        }

        Gamepads.gamepad1.square.whenBecomesTrue {
            Outtake.fullManual = false
        }

        Gamepads.gamepad1.leftStickButton.whenBecomesTrue { sens=0.3 } whenBecomesFalse { sens=1.0 }
    }

    var isHoldingPose: Boolean = false

    val targetThreshold = 0.5

    fun getHeadingTurnPower(targetHeadingDeg: Double, currentHeadingDeg: Double): Double {
        var error = targetHeadingDeg - currentHeadingDeg
        error = ((error + 180) % 360 + 360) % 360 - 180
        if (kotlin.math.abs(error) <= targetThreshold) return 0.0
        val turnPower = min(kotlin.math.abs(error) / 90.0 + 0.15, 1.0)
        return (if (error > 0) -turnPower else turnPower)
    }

    fun robotCentricCalculated(g: Gamepad, targetHeadingDeg: Double, currentHeadingDeg: Double) {
        val y: Float = -g.left_stick_y
        val x: Float = g.left_stick_x
        val t = getHeadingTurnPower(targetHeadingDeg, currentHeadingDeg)
        val d = max((abs(y) + abs(x) + abs(t)), 1.0)
        fl.power = (y + x + t) / d
        fr.power = (y - x - t) / d
        bl.power = (y - x + t) / d
        br.power = (y + x - t) / d
    }

    val telemetry: TelemetryImplUpstreamSubmission by lazy { TelemetryImplUpstreamSubmission(this) }

    override fun onUpdate() {
        val y: Double = -gamepad1.left_stick_y.toDouble() * sens
        val x: Double = gamepad1.left_stick_x.toDouble() * sens
        val t: Double = gamepad1.right_stick_x.toDouble() * sens
        val d = max((abs(y) + abs(x) + abs(t)), 1.0)
        fl.power = (y + x + t) / d
        fr.power = (y - x - t) / d
        bl.power = (y - x + t) / d
        br.power = (y + x - t) / d


        telemetry.addData("Velocity = ",Flywheels.targetVelocity)
        telemetry.addData("Hood Position = ", Hood.hoodPosition)
        telemetry.addData("Is Holding pose", isHoldingPose)
        telemetry.addData("Pose", PedroComponent.follower.pose)
        telemetry.addData("Target", Outtake.targetPose)
        telemetry.update()
    }
}