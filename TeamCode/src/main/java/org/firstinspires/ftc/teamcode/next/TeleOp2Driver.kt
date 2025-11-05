package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.JoinedTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.currentX
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake.currentY
import kotlin.math.pow
import kotlin.math.sqrt

@TeleOp(name="2 Driver TeleOP")
class TeleOp2Driver: NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            SubsystemComponent(Intake, Outtake, DriveTrain),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        // Flap Controls
        Gamepads.gamepad1.dpadUp whenBecomesTrue Outtake.FlapDown
        Gamepads.gamepad1.dpadDown whenBecomesTrue Outtake.FlapUp

        // Gear Controls
        Gamepads.gamepad1.rightBumper whenBecomesTrue { if(!Outtake.auto) Outtake.spinGearRight } whenBecomesFalse { if(!Outtake.auto) Outtake.stopGear }
        Gamepads.gamepad1.leftBumper whenBecomesTrue  Outtake.spinGearLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad1.dpadLeft whenBecomesTrue  Outtake.gearAlittleLeft whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad1.dpadLeft whenBecomesTrue  Outtake.gearAlittleRight whenBecomesFalse Outtake.stopGear


        // Flywheel Outtake Controls
        Gamepads.gamepad1.x whenBecomesTrue Outtake.flywheelOn
        Gamepads.gamepad1.y whenBecomesTrue Outtake.flywheelOff
        Gamepads.gamepad1.b whenBecomesTrue Outtake.flywheelBack

        // Intake Controls
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake whenBecomesFalse Intake.stopIntake
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake whenBecomesFalse Intake.stopIntake

        // Gamepad 2 Controls
        Gamepads.gamepad2.y whenBecomesTrue { Outtake.targetOnVelo = 1200.0 }
        Gamepads.gamepad2.x whenBecomesTrue { Outtake.targetOnVelo = 950.0 }
        Gamepads.gamepad2.a whenBecomesTrue { Outtake.targetOnVelo = 800.0 }
        Gamepads.gamepad2.b whenBecomesTrue { Outtake.targetOnVelo = 750.0 }
    }

    override fun onUpdate() {
        tele.run {

            addData("HP", Outtake.hP)
            addData("gS", Outtake.gS.state)
            addData("Power", Outtake.targetVelo)
            addData("current pose x", Outtake.currentX)
            addData("current pose y", Outtake.currentY)
            addData("outtake", sqrt((Outtake.xcord- currentX).pow(2) + (Outtake.ycord- currentY).pow(2)))
            update()
        }
    }
}
