package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight

@TeleOp
@Disabled
class Gamepad: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent
        )
    }

    override fun onInit() {
        Gamepads.gamepad1.a whenBecomesTrue  { gamepad1.setLedColor(255.0,255.0,0.0, 3000) }
        Gamepads.gamepad1.b whenTrue  { gamepad1.rumble(20 )}
        Gamepads.gamepad1.x whenBecomesTrue {gamepad1.setLedColor(0.0, 0.0, 255.0, 3000) }
    }

    override fun onUpdate() {

    }
}