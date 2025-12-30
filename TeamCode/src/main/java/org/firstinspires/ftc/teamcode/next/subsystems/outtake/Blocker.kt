package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import androidx.core.graphics.component1
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import kotlin.time.Duration.Companion.seconds

object Blocker: Subsystem {
    private val servo: ServoEx = ServoEx("s2")

    private val closedPos: Double = 0.0 // Closed position (blocks)
    private val openPos: Double = 1.0 // Open position (allows balls)

    /**
     * @return Sequential Command for Opening the Blocker Servo
     */
    fun open(): Command = SequentialGroupLocal(
        InstantCommand { servo.position = openPos },
        Delay(0.1.seconds) // Some time
    )

    /**
     * @return Sequential Command for Closing the Blocker Servo
     */
    fun close(): Command = SequentialGroupLocal(
        InstantCommand { servo.position = closedPos },
        Delay(0.1.seconds) // Some time
    )
}