package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.ServoEx

@TeleOp
class Install: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent
        )
    }

    val s1: ServoEx = ServoEx("dS1")
    val s2: ServoEx = ServoEx("dS2")

    override fun onInit() {
        s1.position = 0.5
        s2.position = 0.5
    }

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.leftBumper whenBecomesTrue {s1.position = 0.167; s2.position = 0.167}
        Gamepads.gamepad1.rightBumper whenBecomesTrue {s1.position = 0.833; s2.position= 0.833}
    }
}