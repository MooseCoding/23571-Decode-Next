package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood


@TeleOp
@Disabled
class ShooterPID: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            SubsystemComponent(
                Flywheels,
                Hood,
                Intake,
                Transfer
            )
        )
    }

    var tele: JoinedTelemetry = JoinedTelemetry(telemetry, PanelsTelemetry.ftcTelemetry)

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftBumper whenBecomesTrue Transfer.start()
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.stop()
        Gamepads.gamepad1.a whenBecomesTrue { Hood.hoodPosition = 0.0 }
        Gamepads.gamepad1.dpadUp whenBecomesTrue { Hood.hoodPosition += 0.1 }
        Gamepads.gamepad1.dpadDown whenBecomesTrue { Hood.hoodPosition -= 0.1 }
    }

    override fun onUpdate() {
        tele.update()
    }
}