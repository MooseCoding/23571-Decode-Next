package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.commands.Command
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx

object DriveTrain: Subsystem {
     val fL = MotorEx("frontLeft").reversed()
     val fR = MotorEx("frontRight")
     val bL = MotorEx("backLeft").reversed()
     val bR = MotorEx("backRight")

    override val defaultCommand: Command
        get() = MecanumDriverControlled(
            fL,
            fR,
            bL,
            bR,
            Gamepads.gamepad1.leftStickY.map {it * 0.7},
            Gamepads.gamepad1.leftStickX.map {it * 0.7},
            Gamepads.gamepad1.rightStickX.map {it * 0.7}
        )
}