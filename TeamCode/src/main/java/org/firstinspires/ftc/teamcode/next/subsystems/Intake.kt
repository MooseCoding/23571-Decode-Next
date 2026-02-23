package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx

object Intake: Subsystem {
    val iM = MotorEx("intake")
    @JvmField
    var iP = 0.0

    override fun periodic() {
        iM.power = iP
    }

    /**
     * @return Runs the intake at full power
     */
    fun runIntake(): InstantCommand = InstantCommand {
        if(iP != -1.0) {
            iP = 1.0
        }
    }

    /**
     * @return Reverses the intake at full power
     */
    fun reverseIntake(): InstantCommand = InstantCommand {
        if(iP != 1.0) {
            iP = -1.0
        }
    }

    /**
     * @return Stops the intake
     */
    fun stopIntake(): InstantCommand = InstantCommand {
        iP = 0.0
    }

    fun partialIntake(): InstantCommand = InstantCommand {
        iP = 0.65
    }
}