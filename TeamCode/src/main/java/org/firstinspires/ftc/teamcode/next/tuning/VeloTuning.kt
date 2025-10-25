package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.bylazar.gamepad.Gamepad
import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake

@TeleOp
class VeloTuning(): NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)
    var teleM = FtcDashboard.getInstance().telemetry

    init {
        addComponents(
            SubsystemComponent(Intake, Outtake),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue Outtake.flywheelTarget
    }

    override fun onUpdate() {
        tele.run {
            addData("f1P", Outtake.f1.power)
            addData("f1V", Outtake.f1.velocity)
            addData("f2V", Outtake.f2.velocity)
            addData("controller value", Outtake.controller.calculate(Outtake.f1.state))
            addData("targetV", Outtake.targetVelo)
            addData("flap pos", Outtake.fP)
            addData("gear pos", Outtake.gP)
            addData("iP", Intake.iP)
            update()
        }
        teleM.run {
            addData("f1P", Outtake.f1.power)
            addData("f1V", Outtake.f1.velocity)
            addData("f2V", Outtake.f2.velocity)
            addData("controller value", Outtake.controller.calculate(Outtake.f1.state))
            addData("targetV", Outtake.targetVelo)
            addData("flap pos", Outtake.fP)
            addData("gear pos", Outtake.gP)
            addData("iP", Intake.iP)
            update()
        }
    }
}