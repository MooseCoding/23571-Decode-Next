package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Dist
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI

@TeleOp
class TeleOp: NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            BindingsComponent,
            BulkReadComponent,
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake)
        )
    }

    override fun onStartButtonPressed() {
        // Intake
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()

        // Shoot
        Gamepads.gamepad1.triangle whenBecomesTrue Outtake.shoot()

        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.reverse() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()

        /*

        Gamepad 2 Main Tele Control

         */
        Gamepads.gamepad2.rightTrigger.greaterThan(0.3) whenBecomesTrue Flywheels.spin()
        Gamepads.gamepad2.leftTrigger.greaterThan(0.3) whenBecomesTrue Flywheels.stop()

        /*

        Manual Stuff

         */

        Gamepads.gamepad2.leftBumper whenBecomesTrue Turret.spinLeft() whenBecomesFalse Turret.stopSpin()
        Gamepads.gamepad2.rightBumper whenBecomesTrue Turret.spinRight() whenBecomesFalse Turret.stopSpin()

        Gamepads.gamepad2.cross whenBecomesTrue InstantCommand { Outtake.distance = Dist.CLOSE }
        Gamepads.gamepad2.circle whenBecomesTrue InstantCommand { Outtake.distance = Dist.FAR }

        Gamepads.gamepad2.triangle whenBecomesTrue InstantCommand { Outtake.fullManual = true}

        Gamepads.gamepad2.dpadUp whenBecomesTrue InstantCommand { Hood.hoodPosition += 0.05 }
        Gamepads.gamepad2.dpadDown whenBecomesTrue InstantCommand { Hood.hoodPosition -= 0.05 }

        Gamepads.gamepad2.options whenBecomesTrue InstantCommand {
            when(DriveTrain.alliance) {
                Alliance.RED -> {
                    PedroComponent.follower.pose = Pose(8.0, 8.0, PI/2)
                }
                Alliance.BLUE -> {
                    PedroComponent.follower.pose = Pose(144-8.0, 8.0, PI/2)
                }
            }
        }
    }

    override fun onUpdate() {
        telemetry.update()
    }
}