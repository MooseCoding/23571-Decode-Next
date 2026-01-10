package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.config.Config
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.ftc.ActiveOpMode

@Config
@Configurable
object Hood: Subsystem {
    val hoodServo = ServoEx("hood")

    @JvmField var hoodPosition = 0.25
    /* min is 0.75 max is 0.25*/

    override fun periodic() {
        hoodServo.position = hoodPosition
        ActiveOpMode.telemetry.addData("hood pose", hoodServo.position)
    }
}
