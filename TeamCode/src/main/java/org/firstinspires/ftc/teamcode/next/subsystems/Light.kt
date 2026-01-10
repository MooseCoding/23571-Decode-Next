package org.firstinspires.ftc.teamcode.next.subsystems

import android.bluetooth.BluetoothGatt
import com.acmerobotics.dashboard.config.Config
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import kotlinx.coroutines.internal.RemoveFirstDesc

@Config
object Light: Subsystem{
    val light: ServoEx = ServoEx("pwm")

    @JvmField var color = 0.0

    override fun periodic() {
        light.position = color
    }

    /**
     * @return A command turning the light purple
     */
    fun Purple(): Command = InstantCommand {
        color = 0.7
    }

    /**
     * @return A command turning the light green
     */
    fun Green(): Command = InstantCommand {
        color = 0.5
    }

    /**
     * @return A command turning the light red
     */
    fun Red(): Command = InstantCommand {
        color = 0.2
    }

    /**
     * @return A command turning the light blue
     */
    fun Blue(): Command = InstantCommand {
        color = 0.6
    }
}