package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroDriverControlled
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.DriverControlledCommand
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.commands.HeadingLockThingy
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.bL
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.bR
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.fL
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.fR
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.sensitivity
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Dist
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.wayfinder.PedroPose
import java.lang.Double.max
import java.lang.Math.abs
import kotlin.math.PI
import kotlin.math.atan2

@TeleOp
class TeleOp
    : NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent( DriveTrain, Intake, Outtake, Light, Sensor, Transfer, Limelight)
        )
    }


    override fun onWaitForStart() {

    }

    override fun onInit() {
    }

    val fl =             MotorEx("fL")// Yse
    val fr =             MotorEx("fR")
    val bl =             MotorEx("bL")
    val br =             MotorEx("bR")// Yes

    var turret: Boolean = false

    var sens: Double = 1.0

    override fun onStartButtonPressed() {
        PedroComponent.follower.pose = Pose(DriveTrain.currentX, DriveTrain.currentY, DriveTrain.currentHeading)

        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()

        // Shoot
        Gamepads.gamepad1.triangle whenBecomesTrue  {
            if(PedroComponent.follower.pose.y < 50.0) {
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

        Gamepads.gamepad2.circle whenBecomesTrue {
            Outtake.fullManual = !Outtake.fullManual
        }

        Gamepads.gamepad2.rightTrigger.greaterThan(0.3) whenBecomesTrue {
            Hood.hoodPosition = Outtake.farHood
            Flywheels.targetVelocity = Outtake.farVelocity
        }
        Gamepads.gamepad2.leftTrigger.greaterThan(0.3) whenBecomesTrue {
            Hood.hoodPosition = Outtake.closeHood
            Flywheels.targetVelocity = Outtake.closeVelocity
        }

        Gamepads.gamepad2.leftBumper whenBecomesTrue Turret.spinLeft()
        Gamepads.gamepad2.rightBumper whenBecomesTrue Turret.spinRight()

        Gamepads.gamepad2.dpadUp whenBecomesTrue {
            Hood.hoodPosition -= 0.1
        }
        Gamepads.gamepad2.dpadDown whenBecomesTrue {
            Hood.hoodPosition += 0.1
        }

        Gamepads.gamepad2.dpadLeft whenBecomesTrue {
            Flywheels.targetVelocity -= 50
        }
        Gamepads.gamepad2.dpadRight whenBecomesTrue {
            Flywheels.targetVelocity += 50
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


        Gamepads.gamepad1.rightStickButton.whenBecomesTrue { sens=0.3 } whenBecomesFalse { sens=1.0 }

        Gamepads.gamepad1.dpadUp whenBecomesTrue {
            // LL Relocalization

        }
    }

    var headingLock:Boolean = false

    override fun onUpdate() {
        if(!headingLock) {
            val y = -gamepad1.left_stick_y * sens
            val x = gamepad1.left_stick_x * sens
            val t = gamepad1.right_stick_x * sens

            val d = max((abs(y) + abs(x) + abs(t)).toDouble(), 1.0)
            fl.power = (y + x + t) / d
            fr.power = (y - x - t) / d
            bl.power = (y - x + t) / d
            br.power = (y + x - t) / d
        }
        /*
        else {
            PedroComponent.follower.holdPoint(Pose(PedroComponent.follower.pose.x, PedroComponent.follower.pose.y,
                atan2(Outtake.turretGoalY- PedroComponent.follower.pose.y, Outtake.turretGoalX- PedroComponent.follower.pose.x) - PedroComponent.c
                ))
        }*/

        telemetry.addData("Velocity = ",Flywheels.targetVelocity)
        telemetry.addData("Hood Position = ", Hood.hoodPosition)
        telemetry.update()
    }
}