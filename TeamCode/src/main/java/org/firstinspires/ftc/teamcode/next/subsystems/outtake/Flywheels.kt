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
import org.firstinspires.ftc.teamcode.next.subsystems.Light

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

    /**
     * To determine if we are spinning slow or at PID
     */
    @JvmField var spinSlow = false

    var motorRpm: Double = 0.0

    override fun periodic() {
        motorRpm = f1.velocity * 60.0/28.0

        if(spinSlow) {
            f1.power = 0.4
        }
        else {
            f1.power = flywheelController.calculate(f1.state)
        }

        f2.power = f1.power

        flywheelController.goal = KineticState(0.0, targetVelocity)

        if(f1.velocity >= targetVelocity - 150) { // Figure ts out
            Light.Green().schedule() // If we can shoot, light that SOB up
        }

        ActiveOpMode.telemetry.run {
            addData("targetVelo", targetVelocity)
            addData("Current RPM", motorRpm)
            addData("RPM target", targetVelocity*60.0/28.0)
            addData("flywheel goal", flywheelController.goal)
        }
    }

    /**
     * Update the PID target
     */
    fun updatePid(velocity:Double) {
        targetVelocity = velocity
    }

    /**
     * @return Spins the Flywheel
     */
    fun spin(): Command = InstantCommand {
        spinSlow = false
    }

    /**
     * @return Stops the Flywheel to cruise speed
     */
    fun stop(): Command = InstantCommand {
        spinSlow = true
    }

    /**
     * @return Hard Stop
     */
    fun hardStop(): Command = InstantCommand {
        spinSlow = false
        targetVelocity = 0.0
    }
}