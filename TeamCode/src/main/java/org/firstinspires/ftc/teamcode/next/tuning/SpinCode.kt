package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.next.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer

@TeleOp
class SpinCode: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(Limelight, Intake, Transfer, Spindexer),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightBumper whenBecomesTrue Spindexer.spinRight
        Gamepads.gamepad1.leftBumper whenBecomesTrue Spindexer.spinLeft
    }

    override fun onUpdate() {
        telemetry.update()
    }
}