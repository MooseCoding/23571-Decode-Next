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
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@Configurable
object Intake: Subsystem {
    val iM = MotorEx("iM")

    @JvmField
    var iP = 0.0

    @JvmField
    var outTime = 0.0

    override fun periodic() {
        iM.power = iP
    }

    val runIntake = InstantCommand {
        iP = 1.0 // Some constant
    }

    val reverseIntake = InstantCommand {
        iP = -1.0
    }

    val reverseIntakeSlow = InstantCommand {
        iP = -0.5
    }

    val reverseIntakeVerySlow = InstantCommand {
        iP = -0.2
    }


    val stopIntake = InstantCommand {
        iP = 0.0
    }

    val backOutWith2 = SequentialGroup(
        Intake.reverseIntakeSlow,
        Flywheels.backOut,
        Delay(0.98.seconds),
        Intake.stopIntake,
        Flywheels.spin
    )
}