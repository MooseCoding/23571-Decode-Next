package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.bylazar.opmodecontrol.ActiveOpMode
import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.Command
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance

@Configurable
object DriveTrain: Subsystem {
    val fL = MotorEx("fL")
    val fR = MotorEx("fR")
    val bL = MotorEx("bL")
    val bR = MotorEx("bR")

    @JvmField var alliance:Alliance = Alliance.BLUE
    @JvmField var sensitivity = 0.8
    var currentX = 0.0
    var currentY = 0.0
    var currentHeading = 0.0

    private val width = 16.0
    private val length = 17.0

    var inZone: Boolean = false
    var parked: Boolean = false

    /*override val defaultCommand: Command
        get() = MecanumDriverControlled(
            fL,
            fR,
            bL,
            bR,
            -Gamepads.gamepad1.leftStickY.map {it * sensitivity},
            Gamepads.gamepad1.leftStickX.map {it * sensitivity},
            Gamepads.gamepad1.rightStickX.map {it * sensitivity}
        )*/

    override fun periodic() {
        dev.nextftc.ftc.ActiveOpMode.telemetry.run {
            addData("Is in zone", inZone)
            addData("parked", parked)
        }
        // Make a determination to turn light orange when not parked and another color when parked like green
    }

    override fun initialize() {

    }


}