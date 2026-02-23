package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import com.bylazar.telemetry.PanelsTelemetry
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.ftc.ActiveOpMode

object Hood: Subsystem {
    val hoodServo = ServoEx("hood")
    var restoreTo = 0.0

    @JvmField var hoodPosition = 0.25
    /* min is 0.75 max is 0.25*/

    override fun periodic() {
        if(!ActiveOpMode.opModeInInit) {
            hoodServo.servo.position = hoodPosition
        }

        PanelsTelemetry.telemetry.run {
            addData("Hood position", hoodServo.position)
            addData("restoreTo", restoreTo)
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
