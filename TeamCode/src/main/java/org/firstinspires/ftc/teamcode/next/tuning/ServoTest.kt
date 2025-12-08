package org.firstinspires.ftc.teamcode.next.tuning

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.ServoEx

@Configurable
@TeleOp
@Disabled
class ServoTest: NextFTCOpMode() {
    companion object {
        @JvmField
        var pos = 0.0
    }

    var servo = ServoEx("flap")
    override fun onInit() {

    }

    override fun onUpdate() {
        servo.position = pos
    }
}