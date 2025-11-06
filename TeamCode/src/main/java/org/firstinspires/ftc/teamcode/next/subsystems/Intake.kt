package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToVelocity
import dev.nextftc.hardware.impl.MotorEx
import com.bylazar.configurables.annotations.Configurable

@Configurable
object Intake: Subsystem {
    val iM = MotorEx("iM")

    @JvmField
    var iP = 0.0

    override fun periodic() {
            iM.power = iP
    }

    val runIntake = InstantCommand {
        iP = 1.0 // Some constant
    }

    val reverseIntake = InstantCommand {
        iP = -1.0
    }

    val stopIntake = InstantCommand {
        iP = 0.0
    }
}