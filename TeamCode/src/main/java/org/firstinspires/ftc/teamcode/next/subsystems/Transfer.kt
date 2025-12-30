package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.CRServoEx
import dev.nextftc.hardware.impl.MotorEx

object Transfer: Subsystem {
    private val transferMotor: MotorEx = MotorEx("em0")

    private var power: Double = 0.0

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