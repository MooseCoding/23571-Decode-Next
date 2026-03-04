package org.firstinspires.ftc.teamcode.next.subsystems


import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance

object DriveTrain: Subsystem {
    @JvmField var alliance:Alliance = Alliance.RED
    @JvmField var sensitivity = 0.8
    var currentX = 0.0
    var currentY = 0.0
    var currentHeading = 0.0

    private val width = 16.0
    private val length = 17.0

    var inZone: Boolean = false
    var parked: Boolean = false

    override fun periodic() {

    }

    override fun initialize() {

    }


}