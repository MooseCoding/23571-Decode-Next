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

    @JvmField var flywheelPID = PIDCoefficients(0.00003, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(1/2400.0, 0.0, 0.3)
    private var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField var targetVelocity = 0.0
   
    /**
     * To determine if we are spinning slow or at PID
     */
    @JvmField var spinSlow = false

    var motorRpm: Double = 0.0

    override fun periodic() {
        f1.power = flywheelController.calculate(f1.state)
        flywheelController.goal = KineticState(0.0, targetVelocity)
    }

    /**
     * Update the PID target
     */
    fun updatePid(velocity:Double) {
        //targetVelocity = velocity
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