package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import java.time.Instant

@TeleOp
class MotorLocator: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val m1 = MotorEx("cm0")
    val m2 = MotorEx("cm1")
    val m3 = MotorEx("cm2")
    val m4 = MotorEx("cm3")

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand { m1.power = 1.0 } whenBecomesFalse InstantCommand { m1.power = 0.0 }
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand { m2.power = 1.0 } whenBecomesFalse InstantCommand { m2.power = 0.0 }
        Gamepads.gamepad1.x whenBecomesTrue InstantCommand { m3.power = 1.0 } whenBecomesFalse InstantCommand { m3.power = 0.0 }
        Gamepads.gamepad1.y whenBecomesTrue InstantCommand { m4.power = 1.0 } whenBecomesFalse InstantCommand { m4.power = 0.0}
    }
}