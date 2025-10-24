package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToVelocity
import dev.nextftc.hardware.impl.MotorEx

@Configurable
object Intake: Subsystem {
    private val iM = MotorEx("iM")

    @JvmField
    var iP = 0.0

    @JvmField
    var velocityTrue = false

    private val controller = controlSystem {
        velPid(0.0,0.0,0.0)
        basicFF(0.0,0.0,0.0)
    }

    @JvmField
    var targetOnVelo = 0.0

    var intakeOn:Command = RunToVelocity(controller, targetOnVelo).requires(this).named("IntakeOn")
    var intakeOff:Command = RunToVelocity(controller, 0.0).requires(this).named("IntakeOff")

    override fun periodic() {
        if (velocityTrue) {
            iM.power= controller.calculate(iM.state)
        }
        else {
            iM.power = iP
        }
    }

    val runIntake = InstantCommand {
        iP = 1.0 // Some constant
    }

    val stopIntake = InstantCommand {
        iP = 0.0
    }
}