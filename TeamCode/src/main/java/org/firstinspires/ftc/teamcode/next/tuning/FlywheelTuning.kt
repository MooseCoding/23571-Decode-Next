package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
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
        Gamepads.gamepad1.x whenBecomesTrue Outtake.flywheelOn
        Gamepads.gamepad1.y whenBecomesTrue Outtake.flywheelOff
    }

    override fun onUpdate() {
        tele.run {
            addData("current X", Outtake.currentX)
            addData("f1P", Outtake.f1.power)
            addData("f1V", Outtake.f1.velocity)
            addData("f2V", Outtake.f2.velocity)
            addData("kinetic state", Outtake.f1.state)
            addData("controller", Outtake.controller)
            addData("controller value", Outtake.controller.calculate(Outtake.f1.state))
            addData("targetV", Outtake.targetOnVelo)
            addData("gear pos", Outtake.gP)
            addData("iP", Intake.iP)
            addData("gear power", Outtake.gS.power)
            addData("g pos", Outtake.gS.currentPosition)
            addData("currentAngle", Outtake.turrentAngle)
            addData("prev angle", Outtake.prevAngle)
            addData("total angle", Outtake.totalAngle)
            addData("d heading", Outtake.dHeading)
            addData("outtake turret", Outtake.turretHeading)
            addData("Dist", Outtake.dist)
            addData("flap pos", Outtake.hP)
            update()
        }
    }
}