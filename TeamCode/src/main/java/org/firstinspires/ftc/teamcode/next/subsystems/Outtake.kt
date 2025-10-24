package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.core.commands.Command
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.controllable.RunToVelocity
import dev.nextftc.hardware.impl.MotorEx

@Configurable
object Outtake: Subsystem {
    private val f1 = MotorEx("flywheel1")
    private val f2 = MotorEx("flywheel2").reversed()

    @JvmField
    var f1P = 0.0
    @JvmField
    var f2P = 0.0

    /*private var velocityTrue: Boolean = false

    private val controller = controlSystem {
        posPid(0.0,0.0,0.0)
    }

    val flywheelSpinToVelocity: Command = RunToVelocity(controller, 0.0).requires(this)

    fun calculatePower() {
        if (velocityTrue) {

        }
        else {
            f1P =
        }
    }
     */
    override fun periodic() {
        f1.power=f1P
        f2.power=f2P
    }

    fun runMotors() {
        f1P = 0.5 // Some constant
        f2P = 0.5 // Some other constant
    }

    fun intakeBall() {

    }

}