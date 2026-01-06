package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx

@TeleOp
class TestShooter: NextFTCOpMode() {
    init {
        addComponents(
            BulkReadComponent,
            BindingsComponent
        )
    }

    val f1: MotorEx = MotorEx("m1")
    val f2: MotorEx = MotorEx("m2")

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand { f1.power = -1.0; f2.power = 1.0}
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand { f1.power = 0.0; f2.power = 0.0}
    }

}