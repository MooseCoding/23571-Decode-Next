package org.firstinspires.ftc.teamcode.next

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.groups.ParallelGroup
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
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import java.time.Instant

@TeleOp
class TeleOP: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(Intake, Outtake, DriveTrain, Sensor, Spindexer, Transfer),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        // Gamepads.gamepad1.a whenBecomesTrue Outtake.shoot // Spin up the flywheels

        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue ParallelGroup(
            Intake.runIntake,
            InstantCommand { Spindexer.intaking = true }
        ) whenBecomesFalse ParallelGroup(
            Intake.stopIntake,
            InstantCommand { Spindexer.intaking = false }
        )

        Gamepads.gamepad1.cross whenBecomesTrue Spindexer.spinTo0
        Gamepads.gamepad1.square whenBecomesTrue Spindexer.spinTo2
        Gamepads.gamepad1.circle whenBecomesTrue Spindexer.spinTo1

        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake whenBecomesFalse Intake.stopIntake

        Gamepads.gamepad1.triangle whenBecomesTrue Outtake.shoot()

        // Gamepad 2

        Gamepads.gamepad2.rightTrigger.greaterThan(0.3) whenBecomesTrue InstantCommand { Outtake.distance += 12 }// Increase Distance
        Gamepads.gamepad2.leftTrigger.greaterThan(0.3) whenBecomesTrue InstantCommand { Outtake.distance -= 12 } // Decrease Distance

        Gamepads.gamepad2.dpadUp whenBecomesTrue InstantCommand { Hood.hoodPosition += 0.05 } // Hood Up
        Gamepads.gamepad2.dpadDown whenBecomesTrue InstantCommand { Hood.hoodPosition -= 0.05 }// Down
        Gamepads.gamepad2.dpadLeft whenTrue InstantCommand { Turret.autoTurret = false; Turret.turret.power = 0.1} // Turret Left
        Gamepads.gamepad2.dpadRight whenTrue InstantCommand { Turret.autoTurret = false; Turret.turret.power = -0.1} // Turret Right

        Gamepads.gamepad2.x whenBecomesTrue InstantCommand { Spindexer.sort = !Spindexer.sort } // Sort off
        Gamepads.gamepad2.b whenBecomesTrue InstantCommand { Turret.autoTurret = !Turret.autoTurret }
        Gamepads.gamepad2.y whenBecomesTrue InstantCommand{ Transfer.spinTop = true }
        Gamepads.gamepad2.a whenBecomesTrue InstantCommand { Flywheels.flywheelsOn = !Flywheels.flywheelsOn  }
    }

    override fun onUpdate() {
        telemetry.addData("Transfer Top", Transfer.spinTop)
        telemetry.update()
    }
}