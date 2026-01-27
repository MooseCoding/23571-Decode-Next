package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import com.acmerobotics.dashboard.config.Config
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.ftc.ActiveOpMode

@Config
@Configurable
object Hood: Subsystem {
    val hoodServo = ServoEx("hood")
    var restoreTo = 0.0

    @JvmField var hoodPosition = 0.50
    /* min is 0.75 max is 0.25*/

    override fun periodic() {
        // hoodServo.position = hoodPosition

        ActiveOpMode.telemetry.run {
            addData("Hood position", hoodPosition)
        }
    }
    fun setRestore(): InstantCommand = InstantCommand{
        restoreTo = hoodPosition
    }
    fun sequence( increment: Double): InstantCommand = InstantCommand{
        hoodPosition -= increment //- goes up + goes down
    }
    fun restore(): InstantCommand = InstantCommand{
        hoodPosition = restoreTo
    }
}
