package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.drivetrains.Mecanum
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.constants.PinpointConstants
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer
import com.pedropathing.geometry.Pose
import com.pedropathing.localization.Localizer
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower;
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance

@Configurable
object DriveTrain: Subsystem {
     val fL = MotorEx("frontLeft")
     val fR = MotorEx("frontRight")
     val bL = MotorEx("backLeft")
     val bR = MotorEx("backRight")

    @JvmField
    var alliance = Alliance.RED


    @JvmField
    var sensistivity = 0.6

    override val defaultCommand: Command
        get() = MecanumDriverControlled(
            fL,
            fR,
            bL,
            bR,
            -Gamepads.gamepad1.leftStickY.map {it * sensistivity},
            Gamepads.gamepad1.leftStickX.map {it * sensistivity},
            Gamepads.gamepad1.rightStickX.map {it * sensistivity}
        )
}