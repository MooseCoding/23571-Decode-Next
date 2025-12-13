package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.CRServoEx

object Transfer: Subsystem {
    val topServo: CRServoEx = CRServoEx("cr0")
    val bottomServo: CRServoEx = CRServoEx("cr1")

    var spinBottom:Boolean = false
    var spinTop:Boolean = false

    override fun periodic() {
        topServo.power = if(spinTop) 1.0 else 0.0
        bottomServo.power = -1.0
    }

    val spin:InstantCommand = InstantCommand {
        spinTop = true
        spinBottom = true
    }

    val stop:InstantCommand = InstantCommand {
        spinTop = false
        spinBottom = false
    }
}