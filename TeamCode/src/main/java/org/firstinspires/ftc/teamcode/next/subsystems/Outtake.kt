package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.control.builder.controlSystem
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx

object Outtake: Subsystem {
    private val f1 = MotorEx("flywheel1")
    private val f2 = MotorEx("flywheel2")

    /*private val controller = controlSystem {
        posPid(0.0,0.0,0.0)
    }*/

    // val flywheelSpinToVelocity: Command = RunToVelocity(controller, 0.0).requires(this)

    /*override fun periodic() {
        f1.power=controller.calculate(f1.state)
        f2.power=controller.calculate(f2.state)
    }*/

}