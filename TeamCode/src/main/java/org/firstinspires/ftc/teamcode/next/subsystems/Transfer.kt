package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import kotlin.time.Duration.Companion.seconds

object Transfer: Subsystem {
    val transferMotor: MotorEx = MotorEx("transfer").reversed()

    private var power: Double = 0.0

    /**
     * Balls held by the transfer
     */
    var ballsHeld: Array<Artifact?> = arrayOf(null, null, null)

    lateinit var target:Array<Artifact>

    var currentBall: Int = 0

    override fun periodic() {
        transferMotor.power = power
    }

    /**
     * @return Starts the transfer up at full power
     */
    fun start():InstantCommand = InstantCommand {
        power = 1.0
    }

    /**
     * @return Stops the transfer
     */
    fun stop() :InstantCommand = InstantCommand {
        power = 0.0
    }

    /**
     * @return Spins the transfer in reverse at full power
     */
    fun reverse(): InstantCommand = InstantCommand {
        power = -1.0
    }
}