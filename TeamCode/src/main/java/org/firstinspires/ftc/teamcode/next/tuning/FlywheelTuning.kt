package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake

@TeleOp
class FlywheelTuning(): NextFTCOpMode() {
    var tele = JoinedTelemetry(PanelsTelemetry.ftcTelemetry, telemetry)

    init {
        addComponents(
            SubsystemComponent(Outtake),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
    }

    override fun onUpdate() {
        tele.run {
            addData("f1P", Outtake.f1.power)
            addData("f1V", Outtake.f1.velocity)
            addData("f2V", Outtake.f2.velocity)
            addData("kinetic state", Outtake.f1.state)
            addData("controller", Outtake.controller)
            addData("controller value", Outtake.controller.calculate(Outtake.f1.state))
            addData("targetV", Outtake.targetVelo)
            addData("gear pos", Outtake.gP)
            addData("iP", Intake.iP)
            addData("gear power", Outtake.gS.power)
            update()
        }
    }
}