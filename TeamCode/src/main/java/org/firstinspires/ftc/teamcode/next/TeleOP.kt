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
        // Flap Controls
        Gamepads.gamepad1.dpadUp whenBecomesTrue Outtake.FlapDown
        Gamepads.gamepad1.dpadDown whenBecomesTrue Outtake.FlapUp

        // Gear Controls
        Gamepads.gamepad1.rightBumper whenBecomesTrue  Outtake.spinGearRight whenBecomesFalse Outtake.stopGear
        Gamepads.gamepad1.leftBumper whenBecomesTrue  Outtake.spinGearLeft whenBecomesFalse Outtake.stopGear

        // Flywheel Outtake Controls
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Outtake.flywheelIn whenBecomesFalse Outtake.flywheelOff

        // Intake Controls
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.intakeOn whenBecomesFalse Intake.intakeOff
        Gamepads.gamepad1.y whenBecomesTrue Outtake.intakeBall
    }
}