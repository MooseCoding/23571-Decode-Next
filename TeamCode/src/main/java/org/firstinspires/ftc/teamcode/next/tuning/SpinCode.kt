package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer

@TeleOp
class SpinCode: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Sensor, Limelight, Intake, Transfer, Spindexer),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue Spindexer.spinTo0
        Gamepads.gamepad1.b whenBecomesTrue Spindexer.spinTo1
        Gamepads.gamepad1.x whenBecomesTrue Spindexer.spinTo2
    }

    override fun onUpdate() {
        telemetry.addData("Bal[0", Spindexer.ballsHeld[Spindexer.currentPos])
        telemetry.update()
    }
}