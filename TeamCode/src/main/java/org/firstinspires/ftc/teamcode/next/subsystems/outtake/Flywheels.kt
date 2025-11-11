package org.firstinspires.ftc.teamcode.next.subsystems.outtake

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx

object Flywheels: Subsystem {
    private val f1 = MotorEx("f1M")
    private val f2 = MotorEx("f2M").reversed()

    @JvmField var flywheelPID = PIDCoefficients(0.0033, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    private var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField var targetVelocity = 0.0

    override fun periodic() {
        f1.power = flywheelController.calculate(f1.state)
        f2.power = f1.power
        flywheelController.goal = KineticState(0.0, targetVelocity)
    }

    fun updatePid(velocity:Double) {
        targetVelocity = velocity
    }
}