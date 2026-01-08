package org.firstinspires.ftc.teamcode.next.tuning

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx

object FlywheelPID : Subsystem{
    val f1: MotorEx = MotorEx("em0")
    val f2: MotorEx = MotorEx("em1").reversed()

    var targetVelo = 0.0
    var pid = PIDCoefficients(0.0033,0.0,0.0)
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)

    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
    }
    override fun periodic(){
        f1.power = controller.calculate(f1.state)
        f2.power = f1.power
        controller.goal = KineticState(0.0, targetVelo)
    }

}