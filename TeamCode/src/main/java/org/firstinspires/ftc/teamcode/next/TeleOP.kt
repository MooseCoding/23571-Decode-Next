package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.JoinedTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake

@TeleOp
class TeleOP: NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            SubsystemComponent(Intake, Outtake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        // Intake Controls
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake whenBecomesFalse Intake.stopIntake
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake whenBecomesFalse Intake.stopIntake

        // Gamepad 2
        Gamepads.gamepad2.rightTrigger.greaterThan(0.3) whenBecomesTrue Outtake.aimUp
        Gamepads.gamepad2.leftTrigger.greaterThan(0.3) whenBecomesTrue Outtake.aimDown

        // Flywheel Controls
        Gamepads.gamepad2.a whenBecomesTrue Outtake.flywheelOn
        Gamepads.gamepad2.b whenBecomesTrue Outtake.flywheelOff
        Gamepads.gamepad2.x whenBecomesTrue Outtake.flywheelBack

        // Gear Controls
        Gamepads.gamepad2.rightBumper whenBecomesTrue  Outtake.spinGearRight whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.leftBumper whenBecomesTrue  Outtake.spinGearLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.dpadLeft whenBecomesTrue  Outtake.gearAlittleLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad2.dpadRight whenBecomesTrue  Outtake.gearAlittleRight whenBecomesFalse Outtake.stopGear

        // Flap Controls
        Gamepads.gamepad2.dpadUp whenBecomesTrue Outtake.FlapDown
        Gamepads.gamepad2.dpadDown whenBecomesTrue Outtake.FlapUp

        // Aimbot Controls
        Gamepads.gamepad2.leftStickButton whenBecomesTrue { Outtake.auto = !Outtake.auto }
        Gamepads.gamepad2.rightStickButton whenBecomesTrue { Outtake.autoShoot = !Outtake.autoShoot }
    }

    override fun onUpdate() {
        tele.run {
            addData("Hood Position: ", Outtake.hP)
            addData("Power: ", Outtake.targetVelo)
            addData("Distance in Tiles: ", Outtake.manualAim/24.0)
            update()
        }
    }
}