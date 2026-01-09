package org.firstinspires.ftc.teamcode.next.tuning

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import java.time.Instant

@Config
@TeleOp
@Disabled
class Swyfts: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            BulkReadComponent
        )
    }

    val dt1 = MotorEx("cm0")

    companion object {
        var power = 0.0
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.a whenBecomesTrue InstantCommand { power = 1.0 }
        Gamepads.gamepad1.y whenBecomesTrue InstantCommand { power = 0.5 }
        Gamepads.gamepad1.b whenBecomesTrue InstantCommand { power = 0.0 }
    }

    override fun onUpdate() {
        dt1.power = power
    }
}