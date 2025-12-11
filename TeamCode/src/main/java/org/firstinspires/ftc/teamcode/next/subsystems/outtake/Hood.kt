package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import com.bylazar.configurables.annotations.Configurable

@Configurable
object Hood: Subsystem {
    private val hoodServo = ServoEx("flap")

    @JvmField var hoodPosition = 0.0

    override fun periodic() {
        hoodServo.position = hoodPosition
    }
}
