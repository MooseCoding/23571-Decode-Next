package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import java.lang.Double.max
import java.lang.Math.abs

@TeleOp
class SwyftDrive: NextFTCOpMode() {
    init {
        addComponents(
            BindingsComponent,
            BulkReadComponent
        )
    }

    val fl =             MotorEx("cm0")
    val fr =             MotorEx("cm1").reversed()
    val bl =             MotorEx("cm2")
    val br =             MotorEx("cm3").reversed()

    override fun onStartButtonPressed() {
    }

    override fun onUpdate() {
        val y = -gamepad1.left_stick_y
        val x = gamepad1.left_stick_x
        val t = gamepad1.right_stick_x

        val d = max((abs(y)+abs(x)+abs(t)).toDouble(), 1.0)
        fl.power = (y+x+t)/d
        fr.power = (y-x-t)/d
        bl.power = (y-x+t)/d
        br.power = (y+x-t)/d
    }
}