package org.firstinspires.ftc.teamcode.next.subsystems

import com.acmerobotics.dashboard.config.Config
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
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

@Config
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
}