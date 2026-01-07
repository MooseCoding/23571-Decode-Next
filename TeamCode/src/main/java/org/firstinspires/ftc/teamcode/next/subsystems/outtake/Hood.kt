package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.config.Config
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import com.bylazar.configurables.annotations.Configurable

@Config
@Configurable
object Hood: Subsystem {
    private val hoodServo = ServoEx("s1")

    @JvmField var hoodPosition = 0.0

    override fun periodic() {
        hoodServo.position = hoodPosition
    }
}
