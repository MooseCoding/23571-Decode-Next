package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.MotorEx

object Flywheels: Subsystem {
    val f1 = MotorEx("em0")
    val f2 = MotorEx("em3").reversed()

    @JvmField var flywheelPID = PIDCoefficients(0.0033, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    private var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField var targetVelocity = 0.0
   val f1M = MotorEx("em0")
    val f2M = MotorEx("em3").reversed()
    @JvmField var flywheelsOn = false

    var motorRpm: Double = 0.0

    override fun periodic() {
        motorRpm = f1.velocity * 60.0/28.0

        f1.power = flywheelController.calculate(f1.state)
        f2.power = f1.power
        if (flywheelsOn) {
            flywheelController.goal = KineticState(0.0, targetVelocity)
        }
        else {
            flywheelController.goal = KineticState(0.0, 0.0)
        }

        ActiveOpMode.telemetry.run {
            addData("targetVelo", targetVelocity)
            addData("Current RPM", motorRpm)
            addData("RPM target", targetVelocity*60.0/28.0)
            addData("flywheel goal", flywheelController.goal)
        }
    }

    fun updatePid(velocity:Double) {
        targetVelocity = velocity
    }

    /**
     * @return Spins the Flywheel
     */
    fun spin(): Command = InstantCommand {
        flywheelsOn = true
    }

    /**
     * @return Stops the Flywheel
     */
    val stop = InstantCommand {
        flywheelsOn = false
    }
}