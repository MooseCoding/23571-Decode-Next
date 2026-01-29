package org.firstinspires.ftc.teamcode.next.subsystems

import android.bluetooth.BluetoothGatt
import com.acmerobotics.dashboard.config.Config
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.ServoEx

@Config
object Light: Subsystem{
    val light: ServoEx = ServoEx("pwm")

    @JvmField var color = 0.0

    override fun periodic() {
        light.position = color
        ActiveOpMode.telemetry.run {
            addData("pos", color)
        }
    }

    /**
     * @return A command turning the light purple
     */
    fun Purple(): Command = InstantCommand {
        color = 0.722
    }

    /**
     * @return A command turning the light green
     */
    fun Green(): Command = InstantCommand {
        color = 0.5
    }

    /**
     * @return A command turning the light orange
     */
    fun Orange(): Command = InstantCommand {
        color = 0.333
    }

    /**
     * @return A command turning the light red
     */
    fun Red(): Command = InstantCommand {
        color = 0.277 // Check this
    }

    /**
     * @return A command turning the light blue
     */
    fun Blue(): Command = InstantCommand {
        color = 0.611
    }

    /**
     * @return A command turning the light sage
     */
    fun Sage(): Command = InstantCommand {
        color = 0.444
    }

    /**
     * @return A command turning the light yellow
     */
    fun Yellow(): Command = InstantCommand {
        color = 0.388
    }

    /**
     * @return A command turning the light indigo
     */
    fun Indigo(): Command = InstantCommand {
        color = 0.666
    }

    /**
     * @return A command turning the light azure
     */
    fun Azure(): Command = InstantCommand {
        color = 0.555
    }
}