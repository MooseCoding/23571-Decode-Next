package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood

@TeleOp
@Disabled
class HoodZero: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            SubsystemComponent(Hood)
        )
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue { Hood.hoodPosition = 0.0 }
        Gamepads.gamepad1.dpadUp whenBecomesTrue { Hood.hoodPosition += 0.1 }
        Gamepads.gamepad1.dpadDown whenBecomesTrue { Hood.hoodPosition -= 0.1 }
    }

    override fun onUpdate() {
        telemetry.run {
            addData("Hood position", Hood.hoodPosition)
            update()
        }
    }
}